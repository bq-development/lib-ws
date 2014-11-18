/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import org.mockito.Mockito;

/**
 * @author Alberto J. Rubio
 * 
 */
public class TransformNullBodiesToEmptyObjectsFilterTest {

	private ContainerRequest request;
    private TransformNullBodiesToEmptyObjectsFilter filter;

	@Before
	public void setup() {
		request = mock(ContainerRequest.class);
        filter = new TransformNullBodiesToEmptyObjectsFilter();
	}

	@Test
	public void testTransformNullBodiesToEmptyObjectsFilterPutMethod() throws IOException {
		when(request.getMethod()).thenReturn("PUT");
		when(request.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
		when(request.getEntityInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
		filter.filter(request);
		verify(request, times(1)).setEntityInputStream(Mockito.any());
	}

	@Test
	public void testTransformNullBodiesToEmptyObjectsFilterPostMethod() throws IOException {
		when(request.getMethod()).thenReturn("POST");
		when(request.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
		when(request.getEntityInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
		filter.filter(request);
		verify(request, times(1)).setEntityInputStream(Mockito.any());
	}

	@Test
	public void testTransformNullBodiesToEmptyObjectsFilterGetMethod() throws IOException {
		when(request.getMethod()).thenReturn("GET");
		when(request.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
		when(request.getEntityInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{}));
		filter.filter(request);
		verify(request, times(0)).setEntityInputStream(Mockito.any());
	}
}
