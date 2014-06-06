/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.api.error;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;

/**
 * @author Alexander De Leon
 * 
 */
public class GenericExceptionMapperTest extends ResourceTest {

	private TestResource resourceMock;

	@Override
	protected void setUpResources() throws Exception {
		resourceMock = mock(TestResource.class);
		addProvider(GenericExceptionMapper.class);
		addResource(resourceMock);
	}

	@Test
	public void testUnknowException() {
		Mockito.doThrow(new RuntimeException("test")).when(resourceMock).get();
		ClientResponse response = client().resource("/test").get(ClientResponse.class);
		assertThat(response.getStatus()).isEqualTo(500);
		assertThat(response.getType()).isEqualTo(MediaType.APPLICATION_JSON_TYPE);
		assertThat(response.getEntity(String.class)).contains("\"error\":\"internal_server_error\"");

	}

	@Test
	public void testWebApplicationException() {
		Mockito.doThrow(new WebApplicationException(Response.ok("ok").build())).when(resourceMock).get();
		ClientResponse response = client().resource("/test").get(ClientResponse.class);
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity(String.class)).isEqualTo("ok");

	}

	@Path("/test")
	public interface TestResource {
		@GET
		Response get();
	}

}
