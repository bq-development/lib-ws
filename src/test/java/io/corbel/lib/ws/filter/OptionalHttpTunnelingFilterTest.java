package io.corbel.lib.ws.filter;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Francisco Sanchez on 9/09/15.
 */
@RunWith(MockitoJUnitRunner.class) public class OptionalHttpTunnelingFilterTest {
    public static final String HTTP_TUNNELING = "httpTunneling";
    public static final String EMPTY_BODY_TEST = "";
    public static final String BASIC_BODY_TEST = "testQueryParameter";
    public static final String BASIC_BODY_WITH_QUESTION_MARK_TEST = "?testQueryParameter";
    public static final String NOT_ENCODED_BODY_TEST = "{}";
    public static final String HTTP_METHOD = "GET";

    @Mock private ContainerRequestContext requestMock;
    @Mock private javax.ws.rs.core.UriInfo uriInfoMock;
    @Mock private javax.ws.rs.core.MultivaluedMap<java.lang.String, java.lang.String> requestMultivalueMapMock;

    private OptionalHttpTunnelingFilter filter;

    @Before
    public void setup() {
        filter = new OptionalHttpTunnelingFilter(true);
        reset(requestMock, uriInfoMock, requestMultivalueMapMock);
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        URI uri = new URI("");

        when(requestMock.getUriInfo()).thenReturn(uriInfoMock);
        when(uriInfoMock.getAbsolutePath()).thenReturn(uri);
        when(uriInfoMock.getQueryParameters()).thenReturn(requestMultivalueMapMock);
        when(requestMultivalueMapMock.getFirst(HTTP_TUNNELING)).thenReturn(HTTP_METHOD);

        when(requestMock.getEntityStream()).thenReturn(getInputStreamFromString(BASIC_BODY_TEST));

        filter.filter(requestMock);

        verify(requestMock).setMethod(HTTP_METHOD);
        verify(requestMock).setEntityStream(null);
        verify(requestMock).setRequestUri(eq(new URI("?" + BASIC_BODY_TEST)));
    }

    @Test
    public void testWithQuestionMark() throws IOException, URISyntaxException {
        URI uri = new URI("");

        when(requestMock.getUriInfo()).thenReturn(uriInfoMock);
        when(uriInfoMock.getAbsolutePath()).thenReturn(uri);
        when(uriInfoMock.getQueryParameters()).thenReturn(requestMultivalueMapMock);
        when(requestMultivalueMapMock.getFirst(HTTP_TUNNELING)).thenReturn(HTTP_METHOD);

        when(requestMock.getEntityStream()).thenReturn(getInputStreamFromString(BASIC_BODY_WITH_QUESTION_MARK_TEST));

        filter.filter(requestMock);

        verify(requestMock).setMethod(HTTP_METHOD);
        verify(requestMock).setEntityStream(null);
        verify(requestMock).setRequestUri(eq(new URI(BASIC_BODY_WITH_QUESTION_MARK_TEST)));
    }


    @Test
    public void testWithEmptyBody() throws IOException, URISyntaxException {
        URI uri = new URI("");

        when(requestMock.getUriInfo()).thenReturn(uriInfoMock);
        when(uriInfoMock.getAbsolutePath()).thenReturn(uri);
        when(uriInfoMock.getQueryParameters()).thenReturn(requestMultivalueMapMock);
        when(requestMultivalueMapMock.getFirst(HTTP_TUNNELING)).thenReturn(HTTP_METHOD);

        when(requestMock.getEntityStream()).thenReturn(getInputStreamFromString(EMPTY_BODY_TEST));

        filter.filter(requestMock);

        verify(requestMock).setMethod(HTTP_METHOD);
        verify(requestMock).setEntityStream(null);
        verify(requestMock).setRequestUri(eq(new URI(EMPTY_BODY_TEST)));
    }

    @Test
    public void testNotEncodedBody() throws IOException, URISyntaxException {
        URI uri = new URI("");

        when(requestMock.getUriInfo()).thenReturn(uriInfoMock);
        when(uriInfoMock.getAbsolutePath()).thenReturn(uri);
        when(uriInfoMock.getQueryParameters()).thenReturn(requestMultivalueMapMock);
        when(requestMultivalueMapMock.getFirst(HTTP_TUNNELING)).thenReturn(HTTP_METHOD);
        when(requestMock.getEntityStream()).thenReturn(getInputStreamFromString(NOT_ENCODED_BODY_TEST));

        try {
            filter.filter(requestMock);
        } catch (WebApplicationException wae) {
            verify(requestMock).setMethod(HTTP_METHOD);
            verify(requestMock).setEntityStream(null);
            assertThat(wae.getResponse().getStatus()).isEqualTo(400);
        }
    }

    private InputStream getInputStreamFromString(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }

}
