/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api.error;

import com.bqreaders.silkroad.common.model.Error;
import com.yammer.dropwizard.validation.InvalidEntityException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

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
		List<String> erros = Arrays.asList("error 1", "error 2");
		InvalidEntityException exception = new InvalidEntityException("message", erros);
		Response response = exceptionMapper.toResponse(exception);
		assertThat(response.getStatus()).isEqualTo(422);
		assertThat(response.getMetadata().getFirst("Content-Type").toString()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(((Error) response.getEntity()).getError()).isEqualTo(TEST_ERROR);
		assertThat(((Error) response.getEntity()).getErrorDescription()).contains("error 1");
		assertThat(((Error) response.getEntity()).getErrorDescription()).contains("error 2");
	}
}
