package com.bqreaders.silkroad.common.api.error;

import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.bqreaders.silkroad.common.model.Error;

public class URISyntaxExceptionMapper implements ExceptionMapper<URISyntaxException> {

	@Override
	public Response toResponse(URISyntaxException exception) {
		return ErrorResponseFactory.getInstance().badRequest(new Error("bad_request", exception.getMessage()));
	}

}
