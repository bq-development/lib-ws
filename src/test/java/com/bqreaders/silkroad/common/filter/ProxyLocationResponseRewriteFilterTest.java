package com.bqreaders.silkroad.common.filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.util.StringKeyObjectValueIgnoreCaseMultivaluedMap;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

/**
 * Created by Francisco Sanchez on 4/02/15.
 */
public class ProxyLocationResponseRewriteFilterTest {

	private final MultivaluedMap<String, Object> responseHeaders = new OutBoundHeaders();
	private ContainerRequest request;
	private ContainerResponse response;
	private NoRedirectResponseFilter filter;
	private ProxyLocationResponseRewriteFilter proxyLocationResponseRewriteFilter;

	@Before
	public void setup() {
		proxyLocationResponseRewriteFilter = new ProxyLocationResponseRewriteFilter(true);
	}

	@Test
	public void testFilter() throws URISyntaxException {
		ContainerRequest request = setupContainerRequest("https://proxy-server.io.com/v1.0/endpoint",
				"/proxypass/v1.0/endpoint");
		ContainerResponse response = setupContainerResponse("https://proxy-server.io.com/v1.0/endpoint/123");

		response = proxyLocationResponseRewriteFilter.filter(request, response);

		assertThat(response.getHttpHeaders().getFirst("Location")).isEqualTo(
				new URI("https://proxy-server.io.com/proxypass/v1.0/endpoint/123"));
	}

	@Test
	public void testFilterWhenNotNeedRewrite() throws URISyntaxException {
		ContainerRequest request = setupContainerRequest("https://proxy-server.io.com/v1.0/endpoint",
				"/proxypass/v1.0/endpoint");
		ContainerResponse response = setupContainerResponse("https://other-server.io.com/v1.0/endpoint/123");

		response = proxyLocationResponseRewriteFilter.filter(request, response);

		assertThat(response.getHttpHeaders().getFirst("Location")).isEqualTo(
				new URI("https://other-server.io.com/v1.0/endpoint/123"));
	}

	public ContainerRequest setupContainerRequest(String baseUri, String originalPath) throws URISyntaxException {
		ContainerRequest containerRequest = mock(ContainerRequest.class);
		when(containerRequest.getHeaderValue("X-Forwarded-Uri")).thenReturn(originalPath);
		when(containerRequest.getBaseUri()).thenReturn(new URI(baseUri));
		when(containerRequest.getAbsolutePath()).thenReturn(new URI(baseUri));
		return containerRequest;
	}

	public ContainerResponse setupContainerResponse(String originalPath) throws URISyntaxException {
		StringKeyObjectValueIgnoreCaseMultivaluedMap multivaluedMap = new StringKeyObjectValueIgnoreCaseMultivaluedMap();
		ContainerResponse containerResponse = mock(ContainerResponse.class);
		when(containerResponse.getHttpHeaders()).thenReturn(multivaluedMap);
		multivaluedMap.putSingle("Location", new URI(originalPath));
		return containerResponse;
	}

}
