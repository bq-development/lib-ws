package io.corbel.lib.ws.api.error;

import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class URISyntaxExceptionMapper implements ExceptionMapper<URISyntaxException> {

	@Override
	public Response toResponse(URISyntaxException exception) {
		return ErrorResponseFactory.getInstance().badRequest(new io.corbel.lib.ws.model.Error("bad_request", exception.getMessage()));
	}

}
