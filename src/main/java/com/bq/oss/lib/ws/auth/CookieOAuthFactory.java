package com.bq.oss.lib.ws.auth;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthFactory;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author Rubén Carrasco
 *
 */
public class CookieOAuthFactory<T> extends AuthFactory<String, T> {

    private final OAuthFactory<T> oAuthFactory;
    private HttpServletRequest request;
    private static final String COOKIE_KEY = "token";
    private final String prefix = "Bearer";

    public CookieOAuthFactory(Authenticator<String, T> authenticator, final String realm, final Class<T> generatedClass) {
        super(authenticator);
        this.oAuthFactory = new OAuthFactory<T>(authenticator, realm, generatedClass);
    }

    @Override
    public T provide() {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(COOKIE_KEY)).findFirst();
            if (tokenCookie.isPresent()) {
                oAuthFactory.setRequest(new CustomAuthorizationHeaderHttpServletRequest(request, prefix + " "
                        + tokenCookie.get().getValue()));
                return oAuthFactory.provide();
            }
        }
        return null;

    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
        oAuthFactory.setRequest(request);
    }

    @Override
    public AuthFactory<String, T> clone(boolean required) {
        return oAuthFactory.clone(required);
    }

    @Override
    public Class<T> getGeneratedClass() {
        return oAuthFactory.getGeneratedClass();
    }

    private static class CustomAuthorizationHeaderHttpServletRequest extends HttpServletRequestWrapper {

        private final String customAuthorizationHeader;

        public CustomAuthorizationHeaderHttpServletRequest(HttpServletRequest request, String customAuthorizationHeader) {
            super(request);
            this.customAuthorizationHeader = customAuthorizationHeader;
        }

        @Override
        public String getHeader(String name) {
            if (name.equals(HttpHeaders.AUTHORIZATION)) {
                return customAuthorizationHeader;
            }
            return super.getHeader(name);
        }

    }
}
