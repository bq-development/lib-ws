package io.corbel.lib.ws.filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alberto J. Rubio
 * 
 */
public class CharsetResponseFilterTest {

    private ContainerRequestContext request;
    private ContainerResponseContext response;
    private CharsetResponseFilter filter;
    private final MultivaluedMap<String, Object> responseHeaders = new MultivaluedHashMap();

    @Before
    public void setup() {
        request = mock(ContainerRequestContext.class);
        response = mock(ContainerResponseContext.class);
        when(response.getHeaders()).thenReturn(responseHeaders);
        filter = new CharsetResponseFilter(true);
    }

    @Test
    public void testCharsetResponseFilterWithAtomXml() {
        when(request.getMethod()).thenReturn("GET");
        when(response.getMediaType()).thenReturn(javax.ws.rs.core.MediaType.APPLICATION_ATOM_XML_TYPE);
        filter.filter(request, response);
        assertThat(responseHeaders.getFirst("Content-Type")).isEqualTo(MediaType.APPLICATION_ATOM_XML_TYPE.withCharset("UTF-8"));
    }

    @Test
    public void testCharsetResponseFilterWithUrlEncoded() {
        when(request.getMethod()).thenReturn("GET");
        when(response.getMediaType()).thenReturn(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        filter.filter(request, response);
        assertThat(responseHeaders.getFirst("Content-Type")).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED_TYPE.withCharset("UTF-8"));
    }

    @Test
    public void testCharsetDefinedInRespinse() {
        when(request.getMethod()).thenReturn("GET");
        when(response.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-16"));
        filter.filter(request, response);
        assertThat(responseHeaders.getFirst("Content-Type")).isEqualTo(MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-16"));
    }
}
