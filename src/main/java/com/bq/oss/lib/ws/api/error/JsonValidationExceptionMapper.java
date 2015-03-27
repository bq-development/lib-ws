/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.api.error;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.bq.oss.lib.ws.model.Error;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Alexander De Leon
 * 
 */
public class JsonValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	private final int notProcessableResponseStatus = 422;

	private static final String DEFAULT_ERROR = "invalid_entity";

	private final String error;

	public JsonValidationExceptionMapper(String error) {
		this.error = error;
	}

	public JsonValidationExceptionMapper() {
		this(DEFAULT_ERROR);
	}

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		return Response.status(notProcessableResponseStatus).type(MediaType.APPLICATION_JSON)
				.entity(new Error(error, generateDescription(exception.getConstraintViolations()))).build();
	}

	private String generateDescription(Set<ConstraintViolation<?>> errors) {
		StringBuilder builder = new StringBuilder("Unprocessable Entity:");
		for (ConstraintViolation errorMessage : errors) {
			builder.append(" ").append(errorMessage.getMessage()).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	public class JacksonAdapter implements ExceptionMapper<JsonProcessingException> {

		@Override
		public Response toResponse(JsonProcessingException exception) {
			return JsonValidationExceptionMapper.this.toResponse(new ConstraintViolationException(exception
					.getMessage() +"Json error at " + exception.getLocation(), Collections.emptySet()));
		}

	}
}
