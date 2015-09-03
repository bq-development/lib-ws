package io.corbel.lib.ws.filter;

import static org.mockito.Mockito.*;

import java.net.URISyntaxException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.corbel.lib.token.TokenInfo;
import io.corbel.lib.token.exception.TokenVerificationException;
import io.corbel.lib.token.parser.TokenParser;
import io.corbel.lib.token.reader.TokenReader;

/**
 * @author Alberto J. Rubio
 */
public class AllowRequestWithoutDomainInUriFilterTest {

    private final String unAuthenticatedPathPattern = "v1.0/not_auth";

    private static final String TOKEN = "token";
    private static final String DOMAIN = "test";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_HEADER_WRONG_VALUE = "Wrong";
    private static final String AUTHORIZATION_HEADER_VALUE = "Bearer " + TOKEN;

    private TokenInfo tokenInfo;
    private TokenParser tokenParser;
    private TokenReader tokenReader;
    private ContainerRequestContext request;
    private AllowRequestWithoutDomainInUriFilter allowRequestWithoutDomainInUriFilter;
    private UriBuilder uriBuilder;

    @Before
    public void setup() throws TokenVerificationException {
        uriBuilder = mock(UriBuilder.class);
        tokenInfo = mock(TokenInfo.class);
        tokenReader = mock(TokenReader.class);
        tokenParser = mock(TokenParser.class);
        when(tokenInfo.getDomainId()).thenReturn(DOMAIN);
        when(tokenReader.getInfo()).thenReturn(tokenInfo);
        when(tokenParser.parseAndVerify(Mockito.anyString())).thenReturn(tokenReader);
        when(uriBuilder.replacePath(Mockito.anyString())).thenReturn(uriBuilder);
        allowRequestWithoutDomainInUriFilter = new AllowRequestWithoutDomainInUriFilter(true, tokenParser, unAuthenticatedPathPattern);
    }

    @Test
    public void testFilterWithoutDomain() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/resource:Test", AUTHORIZATION_HEADER_VALUE);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder).replacePath(Mockito.anyString());
        verify(uriBuilder).build();
        verify(request).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUriWithDomain() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/test-qa/resource/test:Test", AUTHORIZATION_HEADER_VALUE);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder, times(0)).replacePath(Mockito.anyString());
        verify(uriBuilder, times(0)).build();
        verify(request, times(0)).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUriWithDifferentDomain() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/different/resource/test:Test", AUTHORIZATION_HEADER_VALUE);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder, times(0)).replacePath(Mockito.anyString());
        verify(uriBuilder, times(0)).build();
        verify(request, times(0)).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUnAuthenticatedUri() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/not_auth", AUTHORIZATION_HEADER_VALUE);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder, times(0)).replacePath(Mockito.anyString());
        verify(uriBuilder, times(0)).build();
        verify(request, times(0)).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUriWithWrongToken() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/test/resource/test:Test", AUTHORIZATION_HEADER_WRONG_VALUE);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder, times(0)).replacePath(Mockito.anyString());
        verify(uriBuilder, times(0)).build();
        verify(request, times(0)).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUriWithoutToken() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/test/resource/test:Test", null);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder, times(0)).replacePath(Mockito.anyString());
        verify(uriBuilder, times(0)).build();
        verify(request, times(0)).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUriWithoutDomainAndTokenWithOptionsMethod() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/resource/test:Test", null);
        when(request.getMethod()).thenReturn(HttpMethod.OPTIONS);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder).replacePath(Mockito.anyString());
        verify(uriBuilder).build();
        verify(request).setRequestUri(Mockito.any());
    }

    @Test
    public void testFilterUriWithoutTokenWithOptionsMethod() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("v1.0/test/resource/test:Test", null);
        when(request.getMethod()).thenReturn(HttpMethod.OPTIONS);
        allowRequestWithoutDomainInUriFilter.filter(request);
        verify(uriBuilder, times(0)).replacePath(Mockito.anyString());
        verify(uriBuilder, times(0)).build();
        verify(request, times(0)).setRequestUri(Mockito.any());
    }

    public ContainerRequestContext setupContainerRequest(String path, String authorizationValue) throws URISyntaxException {
        request = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(path);
        when(uriInfo.getRequestUriBuilder()).thenReturn(uriBuilder);
        when(request.getUriInfo()).thenReturn(uriInfo);
        when(request.getHeaderString(AUTHORIZATION_HEADER)).thenReturn(authorizationValue);
        return request;
    }
}
