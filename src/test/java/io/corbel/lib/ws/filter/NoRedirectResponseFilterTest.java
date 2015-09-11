package io.corbel.lib.ws.filter;

import io.corbel.lib.ws.model.CustomHeaders;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Alberto J. Rubio
 * 
 */
public class NoRedirectResponseFilterTest {

    private ContainerRequestContext request;
    private ContainerResponseContext response;
    private NoRedirectResponseFilter filter;

    private final MultivaluedMap<String, Object> responseHeaders = new MultivaluedHashMap<String, Object>();

    @Before
    public void setup() {
        request = mock(ContainerRequestContext.class);
        response = mock(ContainerResponseContext.class);
        when(response.getHeaders()).thenReturn(responseHeaders);
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
        when(request.getHeaderString(CustomHeaders.NO_REDIRECT_HEADER)).thenReturn("true");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(1)).setStatus(Status.OK.getStatusCode());
    }

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetToFalse() {
        when(response.getStatus()).thenReturn(Status.TEMPORARY_REDIRECT.getStatusCode());
        when(request.getHeaderString(CustomHeaders.NO_REDIRECT_HEADER)).thenReturn("false");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetWithErrorValue() {
        when(response.getStatus()).thenReturn(Status.TEMPORARY_REDIRECT.getStatusCode());
        when(request.getHeaderString(CustomHeaders.NO_REDIRECT_HEADER)).thenReturn("error");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.TEMPORARY_REDIRECT.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testRedirectResponseFilterWithNoRedirectHeaderSetToTrueToStatusOk() {
        when(response.getStatus()).thenReturn(Status.OK.getStatusCode());
        when(request.getHeaderString(CustomHeaders.NO_REDIRECT_HEADER)).thenReturn("true");
        filter.filter(request, response);
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        verify(response, times(0)).setStatus(Status.NO_CONTENT.getStatusCode());
    }
}
