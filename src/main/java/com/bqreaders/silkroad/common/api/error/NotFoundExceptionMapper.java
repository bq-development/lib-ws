/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.bqreaders.silkroad.common.model.Error;
import com.sun.jersey.api.NotFoundException;

/**
 * @author Francisco Sanchez
 * 
 */
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	private static final String DEFAULT_ERROR = "not_found";

	@Override
	public Response toResponse(NotFoundException exception) {
		return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
				.entity(new Error(DEFAULT_ERROR, "URL " + exception.getNotFoundUri().toString() + " not found."))
				.build();
	}

}
