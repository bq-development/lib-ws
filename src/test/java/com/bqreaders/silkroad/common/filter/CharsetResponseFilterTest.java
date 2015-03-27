/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

/**
 * @author Alberto J. Rubio
 * 
 */
public class CharsetResponseFilterTest {

	private ContainerRequest request;
	private ContainerResponse response;
    private CharsetResponseFilter filter;
    private final MultivaluedMap<String, Object> responseHeaders = new OutBoundHeaders();

	@Before
	public void setup() {
		request = mock(ContainerRequest.class);
		response = mock(ContainerResponse.class);
		when(response.getHttpHeaders()).thenReturn(responseHeaders);
        filter = new CharsetResponseFilter(true);
	}

	@Test
	public void testCharsetResponseFilterWithAtomXml() {
		when(request.getMethod()).thenReturn("GET");
		when(response.getMediaType()).thenReturn(javax.ws.rs.core.MediaType.APPLICATION_ATOM_XML_TYPE);
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Content-Type")).isEqualTo("application/atom+xml; charset=UTF-8");
	}

	@Test
	public void testCharsetResponseFilterWithUrlEncoded() {
		when(request.getMethod()).thenReturn("GET");
		when(response.getMediaType()).thenReturn(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Content-Type")).isEqualTo(
				"application/x-www-form-urlencoded; charset=UTF-8");
	}

	@Test
	public void testCharsetDefinedInRespinse() {
		when(request.getMethod()).thenReturn("GET");
		when(response.getMediaType()).thenReturn(
				new MediaType("application", "json", Collections.singletonMap("charset", "UTF-16")));
		filter.filter(request, response);
		assertThat(responseHeaders.getFirst("Content-Type")).isEqualTo("application/json; charset=UTF-16");
	}
}
