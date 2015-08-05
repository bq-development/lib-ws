package io.corbel.lib.ws.auth;

import static java.util.stream.StreamSupport.stream;
import io.dropwizard.auth.oauth.OAuthFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Priority;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.corbel.lib.token.TokenInfo;
import io.corbel.lib.ws.api.error.ErrorResponseFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class is a bit of a hack to Dropwizard(Jersey 2.17). It uses the {@link io.dropwizard.auth.oauth.OAuthFactory} class to obtain an
 * instance of {@link AuthorizationInfo}. The filter is configured to only verify the set of request whose path matches the specified
 * pattern. It validates only access rules of type <b>http_access</b>. If request cannot proceed, it returns a HTTP error 401 without any
 * error information.
 * 
 * @author Alexander De Leon
 * 
 */

@Priority(Priorities.AUTHORIZATION) public class AuthorizationRequestFilter implements ContainerRequestFilter {

    public static final String AUTHORIZATION_INFO_PROPERTIES_KEY = "AuthorizationInfo";

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationRequestFilter.class);

    private final OAuthFactory<AuthorizationInfo> oAuthProvider;
    private final CookieOAuthFactory<AuthorizationInfo> cookieOAuthProvider;
    private final String unAuthenticatedPathPattern;

    @Context private HttpServletRequest request;

    public AuthorizationRequestFilter(OAuthFactory<AuthorizationInfo> provider, CookieOAuthFactory<AuthorizationInfo> cookieOAuthProvider,
            String unAuthenticatedPathPattern) {
        this.oAuthProvider = provider;
        this.cookieOAuthProvider = cookieOAuthProvider;
        this.unAuthenticatedPathPattern = unAuthenticatedPathPattern;
    }

    public AuthorizationRequestFilter() {
        this.oAuthProvider = null;
        this.cookieOAuthProvider = null;
        this.unAuthenticatedPathPattern = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void filter(ContainerRequestContext request) {
        if (!request.getUriInfo().getPath().matches(unAuthenticatedPathPattern)) {
            // OPTIONS is always allowed (for CORS)
            if (!request.getMethod().equals(HttpMethod.OPTIONS)) {

                CustomRequest customRequest = new CustomRequest(getRequest(), request);
                oAuthProvider.setRequest(customRequest);
                AuthorizationInfo info = oAuthProvider.provide();
                if (info == null) {
                    cookieOAuthProvider.setRequest(customRequest);
                    info = cookieOAuthProvider.provide();
                }

                if (info != null) {
                    checkAccessRules(info, request);
                    storeAuthorizationInfoInRequestProperties(info, request);
                } else {
                    throw new WebApplicationException(ErrorResponseFactory.getInstance().unauthorized());
                }
            }
        }
    }

    public void checkAccessRules(final AuthorizationInfo info, final ContainerRequestContext request) {
        Set<JsonObject> applicableRules = Sets.filter(info.getAccessRules(), rule -> matchesMethod(request.getMethod(), rule) && matchesUriPath(request.getUriInfo().getPath(), rule)
                && matchesMediaTypes(request, rule) && matchesTokenType(info.getTokenReader().getInfo(), rule));

        // if no rules apply then by default access is denied
        if (applicableRules.isEmpty()) {
            throw new WebApplicationException(ErrorResponseFactory.getInstance().unauthorized());
        }
    }

    private void storeAuthorizationInfoInRequestProperties(AuthorizationInfo info, ContainerRequestContext request) {
        request.setProperty(AUTHORIZATION_INFO_PROPERTIES_KEY, info);
    }

    private boolean matchesTokenType(TokenInfo token, JsonObject rule) {
        if (!rule.has("tokenType")) {
            // if no tokenType is defined there's nothing to check.
            return true;
        }
        String value = rule.get("tokenType").getAsString();
        switch (value) {
            case "user":
                return token.getUserId() != null;
            default:
                return false; // if we don't know what that value means then fail the rule
        }
    }

    private boolean matchesMethod(String method, JsonObject input) {
        if (!input.has("methods")) {
            LOG.warn("Http access rule without methods field: {}", input.toString());
            return false;
        }
        JsonArray methods = input.get("methods").getAsJsonArray();
        for (JsonElement jsonMethod : methods) {
            if (method.equals(jsonMethod.getAsString())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesMediaTypes(ContainerRequestContext request, JsonObject input) {
        if (!input.has("mediaTypes")) {
            LOG.warn("Http access rule without mediaTypes field: {}", input.toString());
            return false;
        }
        JsonArray mediaTypesArray = input.get("mediaTypes").getAsJsonArray();

        for (MediaType mediaType : request.getAcceptableMediaTypes()) {
            if (stream(mediaTypesArray.spliterator(), true).map(
                    mediatypeJsonElement -> MediaType.valueOf(mediatypeJsonElement.getAsString())).anyMatch(ruleMediaType -> {
                return mediaType.isCompatible(ruleMediaType);
            })) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesUriPath(String path, JsonObject input) {
        if (!input.has("uri")) {
            LOG.warn("Http access rule without uri field: {}", input.toString());
            return false;
        }
        return path.matches(input.get("uri").getAsString());
    }

    // Testing purpose
    public HttpServletRequest getRequest() {
        return this.request;
    }

    public class CustomRequest implements HttpServletRequest {
        private final HttpServletRequest request;
        private final ContainerRequestContext requestContext;

        public CustomRequest(HttpServletRequest request, ContainerRequestContext requestContext) {
            this.request = request;
            this.requestContext = requestContext;
        }

        @Override
        public Object getAttribute(String name) {
            return request.getAttribute(name);
        }

        @Override
        public String getHeader(String name) {
            if (HttpHeader.AUTHORIZATION.is(name)) {
                return requestContext.getHeaderString(name);
            }
            return request.getHeader(name);
        }

        @Override
        public String getAuthType() {
            return request.getAuthType();
        }

        @Override
        public Cookie[] getCookies() {
            return request.getCookies();
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return request.getAttributeNames();
        }

        @Override
        public long getDateHeader(String name) {
            return request.getDateHeader(name);
        }

        @Override
        public String getCharacterEncoding() {
            return request.getCharacterEncoding();
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
            request.setCharacterEncoding(env);
        }

        @Override
        public int getContentLength() {
            return request.getContentLength();
        }

        @Override
        public long getContentLengthLong() {
            return request.getContentLengthLong();
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return request.getHeaders(name);
        }

        @Override
        public String getContentType() {
            return request.getContentType();
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return request.getInputStream();
        }

        @Override
        public String getParameter(String name) {
            return request.getParameter(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return request.getHeaderNames();
        }

        @Override
        public int getIntHeader(String name) {
            return request.getIntHeader(name);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return request.getParameterNames();
        }

        @Override
        public String getMethod() {
            return request.getMethod();
        }

        @Override
        public String[] getParameterValues(String name) {
            return request.getParameterValues(name);
        }

        @Override
        public String getPathInfo() {
            return request.getPathInfo();
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return request.getParameterMap();
        }

        @Override
        public String getPathTranslated() {
            return request.getPathTranslated();
        }

        @Override
        public String getProtocol() {
            return request.getProtocol();
        }

        @Override
        public String getScheme() {
            return request.getScheme();
        }

        @Override
        public String getContextPath() {
            return request.getContextPath();
        }

        @Override
        public String getServerName() {
            return request.getServerName();
        }

        @Override
        public int getServerPort() {
            return request.getServerPort();
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return request.getReader();
        }

        @Override
        public String getQueryString() {
            return request.getQueryString();
        }

        @Override
        public String getRemoteUser() {
            return request.getRemoteUser();
        }

        @Override
        public String getRemoteAddr() {
            return request.getRemoteAddr();
        }

        @Override
        public String getRemoteHost() {
            return request.getRemoteHost();
        }

        @Override
        public boolean isUserInRole(String role) {
            return request.isUserInRole(role);
        }

        @Override
        public void setAttribute(String name, Object o) {
            request.setAttribute(name, o);
        }

        @Override
        public Principal getUserPrincipal() {
            return request.getUserPrincipal();
        }

        @Override
        public void removeAttribute(String name) {
            request.removeAttribute(name);
        }

        @Override
        public String getRequestedSessionId() {
            return request.getRequestedSessionId();
        }

        @Override
        public Locale getLocale() {
            return request.getLocale();
        }

        @Override
        public String getRequestURI() {
            return request.getRequestURI();
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return request.getLocales();
        }

        @Override
        public boolean isSecure() {
            return request.isSecure();
        }

        @Override
        public StringBuffer getRequestURL() {
            return request.getRequestURL();
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return request.getRequestDispatcher(path);
        }

        @Override
        public String getServletPath() {
            return request.getServletPath();
        }

        @Override
        public String getRealPath(String path) {
            return request.getRealPath(path);
        }

        @Override
        public HttpSession getSession(boolean create) {
            return request.getSession(create);
        }

        @Override
        public int getRemotePort() {
            return request.getRemotePort();
        }

        @Override
        public String getLocalName() {
            return request.getLocalName();
        }

        @Override
        public String getLocalAddr() {
            return request.getLocalAddr();
        }

        @Override
        public int getLocalPort() {
            return request.getLocalPort();
        }

        @Override
        public ServletContext getServletContext() {
            return request.getServletContext();
        }

        @Override
        public HttpSession getSession() {
            return request.getSession();
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return request.startAsync();
        }

        @Override
        public String changeSessionId() {
            return request.changeSessionId();
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return request.isRequestedSessionIdValid();
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return request.isRequestedSessionIdFromCookie();
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return request.isRequestedSessionIdFromURL();
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return request.isRequestedSessionIdFromUrl();
        }

        @Override
        public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
            return request.authenticate(response);
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return request.startAsync(servletRequest, servletResponse);
        }

        @Override
        public void login(String username, String password) throws ServletException {
            request.login(username, password);
        }

        @Override
        public void logout() throws ServletException {
            request.logout();
        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return request.getParts();
        }

        @Override
        public boolean isAsyncStarted() {
            return request.isAsyncStarted();
        }

        @Override
        public boolean isAsyncSupported() {
            return request.isAsyncSupported();
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return request.getPart(name);
        }

        @Override
        public AsyncContext getAsyncContext() {
            return request.getAsyncContext();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return request.getDispatcherType();
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
            return request.upgrade(handlerClass);
        }

    }

}
