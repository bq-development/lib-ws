package io.corbel.lib.ws.api.error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author Francisco Sanchez
 * 
 */
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final String DEFAULT_ERROR = "not_found";

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
                .entity(new io.corbel.lib.ws.model.Error(DEFAULT_ERROR, "URL requested not found.")).build();
    }

}
