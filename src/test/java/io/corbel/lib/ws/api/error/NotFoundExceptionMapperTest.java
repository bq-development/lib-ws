package io.corbel.lib.ws.api.error;

import static org.fest.assertions.api.Assertions.assertThat;

import java.net.URISyntaxException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import io.corbel.lib.ws.model.Error;

/**
 * @author Francisco Sanchez
 * 
 */
public class NotFoundExceptionMapperTest {

    private static final String TEST_ERROR = "not_found";
    private static final String URI_STRING = "http://test.io";

    private NotFoundExceptionMapper exceptionMapper;

    @Before
    public void setup() {
        exceptionMapper = new NotFoundExceptionMapper();
    }

    @Test
    public void testExceptionIsMappedCorrectly() throws URISyntaxException {
        NotFoundException exception = new NotFoundException("message");
        Response response = exceptionMapper.toResponse(exception);
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMetadata().getFirst("Content-Type").toString()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(((Error) response.getEntity()).getError()).isEqualTo(TEST_ERROR);
    }
}
