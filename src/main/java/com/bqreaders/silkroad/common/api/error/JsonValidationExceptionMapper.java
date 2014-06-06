/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api.error;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.bqreaders.silkroad.common.model.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.yammer.dropwizard.validation.InvalidEntityException;

/**
 * @author Alexander De Leon
 * 
 */
public class JsonValidationExceptionMapper implements ExceptionMapper<InvalidEntityException> {

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
	public Response toResponse(InvalidEntityException exception) {
		return Response.status(notProcessableResponseStatus).type(MediaType.APPLICATION_JSON)
				.entity(new Error(error, generateDescription(exception.getErrors()))).build();
	}

	private String generateDescription(ImmutableList<String> errors) {
		StringBuilder builder = new StringBuilder("Unprocessable Entity:");
		for (String errorMessage : errors) {
			builder.append(" ").append(errorMessage).append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	public class JacksonAdapter implements ExceptionMapper<JsonProcessingException> {

		@Override
		public Response toResponse(JsonProcessingException exception) {
			return JsonValidationExceptionMapper.this.toResponse(new InvalidEntityException(exception.getMessage(),
					Arrays.asList("Json error at " + exception.getLocation().toString())));
		}

	}
}
