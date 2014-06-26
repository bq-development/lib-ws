/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.filter;

import com.bqreaders.silkroad.common.model.CustomHeaders;
import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alberto J. Rubio
 * 
 */
public class NoRedirectResponseFilterTest {

	private ContainerRequest request;
	private ContainerResponse response;
    private NoRedirectResponseFilter filter;

	private final MultivaluedMap<String, Object> responseHeaders = new OutBoundHeaders();

    @Before
	public void setup() {
		request = mock(ContainerRequest.class);
		response = mock(ContainerResponse.class);
		when(response.getHttpHeaders()).thenReturn(responseHeaders);
        filter = new NoRedirectResponseFilter(true);
    }

	@Test
	public void testRedirectResponseFilterWithoutNoRedirectHeader() {
		filter.filter(request, response);
        when(response.getStatus()).thenReturn(Status.TEMPORARY_REDIRECT.getStatusCode());
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
	}

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetToTrue() {
        when(response.getStatus()).thenReturn(Status.TEMPORARY_REDIRECT.getStatusCode());
        when(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.getValue())).thenReturn("true");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(1)).setStatus(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetToFalse() {
        when(response.getStatus()).thenReturn(Status.TEMPORARY_REDIRECT.getStatusCode());
        when(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.getValue())).thenReturn("false");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetWithErrorValue() {
        when(response.getStatus()).thenReturn(Status.TEMPORARY_REDIRECT.getStatusCode());
        when(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.getValue())).thenReturn("error");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetToTrueToStatusOk() {
        when(response.getStatus()).thenReturn(Status.OK.getStatusCode());
        when(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.getValue())).thenReturn("true");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
    }
}
