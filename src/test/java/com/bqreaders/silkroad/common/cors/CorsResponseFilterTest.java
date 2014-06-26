/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.cors;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import com.bqreaders.silkroad.common.model.CustomHeaders;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

/**
 * @author Alexander De Leon
 * 
 */
public class CorsResponseFilterTest {

	private static final int TEST_PREFLIGHT_MAX_AGE = 10;
	private static final String TEST_ORIGIN = "http://example.org";
	private static final String TEST_ORIGIN2 = "http://example2.org";

	private ContainerRequest request;
	private ContainerResponse response;
	private final MultivaluedMap<String, Object> responseHeaders = new OutBoundHeaders();

	@Before
	public void setup() {
		request = mock(ContainerRequest.class);
		response = mock(ContainerResponse.class);
		when(response.getHttpHeaders()).thenReturn(responseHeaders);
	}

	@Test
	public void testDisabled() {
		CorsResponseFilter filter = CorsResponseFilter.disabled();
		when(request.getHeaderValue("Origin")).thenReturn(TEST_ORIGIN);
		when(request.getMethod()).thenReturn("GET");
		filter.filter(request, response);
		assertThat(responseHeaders.isEmpty()).isTrue();
	}

	@Test
	public void testDisabledPreflight() {
		CorsResponseFilter filter = CorsResponseFilter.disabled();
		when(request.getHeaderValue("Origin")).thenReturn(TEST_ORIGIN);
		when(request.getMethod()).thenReturn("OPTIONS");
		responseHeaders.add("Allow", "GET");
		filter.filter(request, response);
		assertThat(responseHeaders.size()).isEqualTo(1);
	}

	@Test
	public void testAnyOrigin() {
		CorsResponseFilter filter = CorsResponseFilter.anyOrigin(TEST_PREFLIGHT_MAX_AGE);
		when(request.getHeaderValue("Origin")).thenReturn(TEST_ORIGIN);
		when(request.getMethod()).thenReturn("GET");
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Access-Control-Allow-Origin")).isEqualTo(TEST_ORIGIN);
	}

	@Test
	public void testAnyOriginPreflight() {
		CorsResponseFilter filter = CorsResponseFilter.anyOrigin(TEST_PREFLIGHT_MAX_AGE);
		when(request.getHeaderValue("Origin")).thenReturn(TEST_ORIGIN);
		when(request.getMethod()).thenReturn("OPTIONS");
		responseHeaders.add("Allow", "GET");
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Access-Control-Allow-Origin")).isEqualTo(TEST_ORIGIN);
		assertThat(responseHeaders.getFirst("Access-Control-Max-Age")).isEqualTo(TEST_PREFLIGHT_MAX_AGE);
		assertThat(responseHeaders.getFirst("Access-Control-Allow-Methods")).isEqualTo("GET");
		assertThat(responseHeaders.getFirst("Access-Control-Expose-Headers")).isEqualTo("Location");
		String[] allowedHeaders = ((String) responseHeaders.getFirst("Access-Control-Allow-Headers")).split(",");
        assertThat(allowedHeaders).contains(HttpHeaders.AUTHORIZATION);
        assertThat(allowedHeaders).contains(HttpHeaders.ACCEPT);
        assertThat(allowedHeaders).contains(HttpHeaders.CONTENT_TYPE);
        assertThat(allowedHeaders).contains(CustomHeaders.NO_REDIRECT_HEADER.getValue());
	}

	@Test
	public void testAllowedOrigins() {
		CorsResponseFilter filter = CorsResponseFilter.onlyAllowedOrigins(TEST_PREFLIGHT_MAX_AGE, TEST_ORIGIN,
				TEST_ORIGIN2);
		when(request.getHeaderValue("Origin")).thenReturn("http://somehost.com");
		when(request.getMethod()).thenReturn("GET");
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Access-Control-Allow-Origin")).isEqualTo(TEST_ORIGIN + " " + TEST_ORIGIN2);
	}

	@Test
	public void testAllowedOriginsPreflight() {
		CorsResponseFilter filter = CorsResponseFilter.onlyAllowedOrigins(TEST_PREFLIGHT_MAX_AGE, TEST_ORIGIN,
				TEST_ORIGIN2);
		when(request.getHeaderValue("Origin")).thenReturn("http://somehost.com");
		when(request.getMethod()).thenReturn("OPTIONS");
		responseHeaders.add("Allow", "GET");
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Access-Control-Allow-Origin")).isEqualTo(TEST_ORIGIN + " " + TEST_ORIGIN2);
		assertThat(responseHeaders.getFirst("Access-Control-Max-Age")).isEqualTo(TEST_PREFLIGHT_MAX_AGE);
		assertThat(responseHeaders.getFirst("Access-Control-Allow-Methods")).isEqualTo("GET");
		assertThat(responseHeaders.getFirst("Access-Control-Expose-Headers")).isEqualTo("Location");
		String[] allowedHeaders = ((String) responseHeaders.getFirst("Access-Control-Allow-Headers")).split(",");
		assertThat(allowedHeaders).contains(HttpHeaders.AUTHORIZATION);
		assertThat(allowedHeaders).contains(HttpHeaders.ACCEPT);
		assertThat(allowedHeaders).contains(HttpHeaders.CONTENT_TYPE);
        assertThat(allowedHeaders).contains(CustomHeaders.NO_REDIRECT_HEADER.getValue());
	}
}
