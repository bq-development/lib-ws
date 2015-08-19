package io.corbel.lib.ws.auth;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthFactory;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

import io.corbel.lib.token.TokenInfo;
import io.corbel.lib.token.reader.TokenReader;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Alexander De Leon
 *
 */
public class AuthorizationRequestFilterTest {

    private static final String TEST_TOKEN = "test_token";

    private static final String TEST_PATH = "v1.0/test";

    private static final String TEST_USER = "user";
    private static final String TEST_NOT_SECURIZED_PATH = "/not_secure_path";

    private CookieOAuthFactory<AuthorizationInfo> cookieProvider;
    private ContainerRequestContext requestMock;
    private AuthorizationInfo authorizationInfoMock;
    private final JsonParser jsonParser = new JsonParser();
    private Authenticator<String, AuthorizationInfo> authenticator;
    private OAuthFactory<AuthorizationInfo> oAuthFactory;
    private HttpServletRequest servletRequest;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws AuthenticationException {
        requestMock = mock(ContainerRequestContext.class);
        authorizationInfoMock = mock(AuthorizationInfo.class);
        authenticator = mock(Authenticator.class);
        servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + TEST_TOKEN);
        when(authenticator.authenticate(TEST_TOKEN)).thenReturn(com.google.common.base.Optional.of(authorizationInfoMock));
        oAuthFactory = new OAuthFactory<>(authenticator, "realm", AuthorizationInfo.class);
        cookieProvider = new CookieOAuthFactory<>(authenticator, "realm", AuthorizationInfo.class);

    }

    @Test(expected = WebApplicationException.class)
    public void testPatternMatches() {
        AuthorizationRequestFilter filter = stubFilter(TEST_NOT_SECURIZED_PATH);
        stubRequest(TEST_PATH, HttpMethod.GET);
        filter.filter(requestMock);
    }

    @Test
    public void testPatternNoMatches() {
        AuthorizationRequestFilter filter = stubFilter(TEST_PATH);
        stubRequest(TEST_PATH, HttpMethod.GET);
        filter.filter(requestMock);
    }

    @Test
    public void testCORSIsSkiped() {
        AuthorizationRequestFilter filter = stubFilter(".*");
        stubRequest(TEST_PATH, HttpMethod.OPTIONS);
        filter.filter(requestMock);
    }

    @Test
    public void testHttpAccessRule() {
        TokenReader tokenReader = mock(TokenReader.class);
        when(authorizationInfoMock.getTokenReader()).thenReturn(tokenReader);
        AuthorizationRequestFilter filter = stubFilter(".*");
        stubRequest(TEST_PATH, HttpMethod.GET);
        when(requestMock.getAcceptableMediaTypes()).thenReturn(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        stubRules(jsonParser
                .parse("{\"type\":\"http_access\", \"mediaTypes\":[ \"application/json\"], \"methods\":[\"GET\"], \"uri\": \"" + TEST_PATH
                        + "\"}").getAsJsonObject());
        filter.filter(requestMock);
    }

    @Test
    public void testHttpAccessRuleWithGenericMediaType() {
        TokenReader tokenReader = mock(TokenReader.class);
        when(authorizationInfoMock.getTokenReader()).thenReturn(tokenReader);
        AuthorizationRequestFilter filter = stubFilter(".*");
        stubRequest(TEST_PATH, HttpMethod.GET);
        when(requestMock.getAcceptableMediaTypes()).thenReturn(Arrays.asList(MediaType.TEXT_HTML_TYPE, MediaType.APPLICATION_JSON_TYPE));
        stubRules(jsonParser.parse(
                "{\"type\":\"http_access\", \"mediaTypes\":[ \"music/mp3\",\"text/plain\",\"application/*\"], \"methods\":[\"GET\"], \"uri\": \""
                        + TEST_PATH + "\"}").getAsJsonObject());
        filter.filter(requestMock);
    }

    @Test(expected = WebApplicationException.class)
    public void testNoRules() {
        AuthorizationRequestFilter filter = stubFilter(TEST_NOT_SECURIZED_PATH);
        stubRequest(TEST_PATH, HttpMethod.GET);
        when(requestMock.getAcceptableMediaTypes()).thenReturn(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        stubRules(); // no rules
        filter.filter(requestMock);
    }

    @Test
    public void testTokenTypeRule() {
        TokenReader tokenReader = mock(TokenReader.class);
        TokenInfo tokenMock = mock(TokenInfo.class);
        when(tokenReader.getInfo()).thenReturn(tokenMock);
        when(tokenMock.getUserId()).thenReturn(TEST_USER);
        when(authorizationInfoMock.getTokenReader()).thenReturn(tokenReader);
        AuthorizationRequestFilter filter = stubFilter(".*");
        when(requestMock.getAcceptableMediaTypes()).thenReturn(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        stubRequest(TEST_PATH, HttpMethod.GET);
        stubRules(jsonParser.parse(
                "{\"type\":\"http_access\", \"mediaTypes\":[ \"application/json\"], \"methods\":[\"GET\"], \"uri\": \"" + TEST_PATH
                        + "\", \"tokenType\":\"user\"}").getAsJsonObject());
        filter.filter(requestMock);
    }

    @Test(expected = WebApplicationException.class)
    public void testTokenTypeRuleAccessDenied() {
        TokenInfo tokenMock = mock(TokenInfo.class);
        TokenReader tokenReader = mock(TokenReader.class);
        when(tokenReader.getInfo()).thenReturn(tokenMock);
        when(tokenMock.getUserId()).thenReturn(null);
        when(authorizationInfoMock.getTokenReader()).thenReturn(tokenReader);
        AuthorizationRequestFilter filter = stubFilter(TEST_NOT_SECURIZED_PATH);
        when(requestMock.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        stubRequest(TEST_PATH, HttpMethod.GET);
        stubRules(jsonParser.parse(
                "{\"type\":\"http_access\", \"mediaTypes\":[ \"application/json\"], \"methods\":[\"GET\"], \"uri\": \"" + TEST_PATH
                        + "\", \"tokenType\":\"user\"}").getAsJsonObject());
        filter.filter(requestMock);
    }

    @Test
    public void cookieTest() {
        TokenReader tokenReader = mock(TokenReader.class);
        when(authorizationInfoMock.getTokenReader()).thenReturn(tokenReader);
        AuthorizationRequestFilter filter = stubFilter("");
        stubRequest(TEST_PATH, HttpMethod.GET);
        when(requestMock.getAcceptableMediaTypes()).thenReturn(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        stubRules(jsonParser
                .parse("{\"type\":\"http_access\", \"mediaTypes\":[ \"application/json\"], \"methods\":[\"GET\"], \"uri\": \"" + TEST_PATH
                        + "\"}").getAsJsonObject());

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(servletRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("token", TEST_TOKEN)});
        filter.filter(requestMock);
    }

    @Test(expected = WebApplicationException.class)
    public void badDomainTest() {
        TokenReader tokenReader = mock(TokenReader.class);
        when(authorizationInfoMock.getTokenReader()).thenReturn(tokenReader);
        AuthorizationRequestFilter filter = stubFilter("");
        stubRequest(TEST_PATH, HttpMethod.GET);
        when(requestMock.getAcceptableMediaTypes()).thenReturn(Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(servletRequest.getCookies()).thenReturn(new Cookie[] {new Cookie("token", TEST_TOKEN)});
        filter.filter(requestMock);
    }

    private void stubRules(JsonObject... rules) {
        when(authorizationInfoMock.getAccessRules()).thenReturn(Sets.newHashSet(rules));
    }

    private void stubRequest(String path, String method) {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(path);
        when(requestMock.getUriInfo()).thenReturn(uriInfo);
        when(requestMock.getMethod()).thenReturn(method);
    }

    private AuthorizationRequestFilter stubFilter(String path) {
        AuthorizationRequestFilter filter = spy(new AuthorizationRequestFilter(oAuthFactory, cookieProvider, path));
        doReturn(servletRequest).when(filter).getRequest();
        return filter;
    }
}
