package io.corbel.lib.ws.filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Francisco Sanchez
 */
public class ProxyLocationResponseRewriteFilterTest {

    private final MultivaluedMap<String, Object> responseHeaders = new MultivaluedHashMap<String, Object>();
    private ContainerRequestContext request;
    private ContainerResponseContext response;
    private NoRedirectResponseFilter filter;
    private ProxyLocationResponseRewriteFilter proxyLocationResponseRewriteFilter;

    @Before
    public void setup() {
        proxyLocationResponseRewriteFilter = new ProxyLocationResponseRewriteFilter(true);
    }

    @Test
    public void testFilter() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("https://proxy-server.io.com/v1.0/endpoint", "/proxypass/v1.0/endpoint");
        ContainerResponseContext response = setupContainerResponse("https://proxy-server.io.com/v1.0/endpoint/123");

        proxyLocationResponseRewriteFilter.filter(request, response);

        assertThat(response.getHeaders().getFirst("Location"))
                .isEqualTo(new URI("https://proxy-server.io.com/proxypass/v1.0/endpoint/123"));
    }

    @Test
    public void testFilterWhenNotNeedRewrite() throws URISyntaxException {
        ContainerRequestContext request = setupContainerRequest("https://proxy-server.io.com/v1.0/endpoint", "/proxypass/v1.0/endpoint");
        ContainerResponseContext response = setupContainerResponse("https://other-server.io.com/v1.0/endpoint/123");

        proxyLocationResponseRewriteFilter.filter(request, response);

        assertThat(response.getHeaders().getFirst("Location")).isEqualTo(new URI("https://other-server.io.com/v1.0/endpoint/123"));
    }

    public ContainerRequestContext setupContainerRequest(String baseUri, String originalPath) throws URISyntaxException {
        ContainerRequestContext containerRequest = mock(ContainerRequestContext.class);
        when(containerRequest.getHeaderString("X-Forwarded-Uri")).thenReturn(originalPath);
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(new URI(baseUri));
        when(uriInfo.getAbsolutePath()).thenReturn(new URI(baseUri));
        when(containerRequest.getUriInfo()).thenReturn(uriInfo);
        return containerRequest;
    }

    public ContainerResponseContext setupContainerResponse(String originalPath) throws URISyntaxException {
        MultivaluedMap<String, Object> multivaluedMap = new MultivaluedHashMap<String, Object>();
        ContainerResponseContext containerResponse = mock(ContainerResponseContext.class);
        when(containerResponse.getHeaders()).thenReturn(multivaluedMap);
        multivaluedMap.putSingle("Location", new URI(originalPath));
        return containerResponse;
    }

}
