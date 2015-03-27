/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api.error;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Collections;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.bqreaders.silkroad.common.model.Error;

/**
 * @author Alexander De Leon
 * 
 */
public class JsonValidationExceptionMapperTest {

	private static final String TEST_ERROR = "the_error";
	private JsonValidationExceptionMapper exceptionMapper;

	@Before
	public void setup() {
		exceptionMapper = new JsonValidationExceptionMapper(TEST_ERROR);
	}

	@Test
	public void testExceptionIsMappedCorrectly() {
		ConstraintViolationException exception = new ConstraintViolationException("message", Collections.emptySet());
		Response response = exceptionMapper.toResponse(exception);
		assertThat(response.getStatus()).isEqualTo(422);
		assertThat(response.getMetadata().getFirst("Content-Type").toString()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(((Error) response.getEntity()).getError()).isEqualTo(TEST_ERROR);
	}
}
