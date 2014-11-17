/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.gson;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.gson.*;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.mockito.verification.VerificationMode;

/**
 * @author Alexander De Leon
 * 
 */
public class GsonMessageReaderWriterProviderTest {

	private static final GsonMessageReaderWriterProvider provider = spy(new GsonMessageReaderWriterProvider());
	private static final TestResource resource = mock(TestResource.class);

	@ClassRule
	public static final ResourceTestRule RULE = ResourceTestRule.builder().addResource(resource).addProvider(provider)
			.build();

	@Test
	public void testGetJsonObjectResponse() {

		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive("1"));
		when(resource.getJsonObjectResponse()).thenReturn(Response.ok().entity(json).build());

		ClientResponse response = RULE.client().resource("/test/objRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonObject()).isTrue();
		assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

	}

	@Test
	public void testGetJsonObjectResponseEmpty() {

		when(resource.getJsonObjectResponse()).thenReturn(Response.ok().build());

		ClientResponse response = RULE.client().resource("/test/objRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		assertThat(response.getEntity(String.class)).isEqualTo("");
	}

	@Test
	public void testGetJsonObjectResponseNull() {

		when(resource.getJsonObjectResponse()).thenReturn(Response.ok().entity(null).build());

		ClientResponse response = RULE.client().resource("/test/objRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		assertThat(response.getEntity(String.class)).isEqualTo("");
	}

	@Test
	public void testGetJsonObject() {

		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive("1"));
		when(resource.getJsonObject()).thenReturn(json);

		ClientResponse response = RULE.client().resource("/test/obj").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonObject()).isTrue();
		assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

	}

	@Test
	public void testGetJsonArrayResponse() {

		JsonArray json = new JsonArray();
		json.add(new JsonPrimitive("1"));
		json.add(new JsonPrimitive("2"));
		when(resource.getJsonArrayResponse()).thenReturn(Response.ok().entity(json).build());

		ClientResponse response = RULE.client().resource("/test/arrayRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonArray()).isTrue();
		assertThat(responseJson.getAsJsonArray().get(0).getAsString()).isEqualTo("1");
		assertThat(responseJson.getAsJsonArray().get(1).getAsString()).isEqualTo("2");
		assertThat(responseJson.getAsJsonArray().size()).isEqualTo(2);

	}

	@Test
	public void testGetJsonArray() {

		JsonArray json = new JsonArray();
		json.add(new JsonPrimitive("1"));
		json.add(new JsonPrimitive("2"));
		when(resource.getJsonArray()).thenReturn(json);

		ClientResponse response = RULE.client().resource("/test/array").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonArray()).isTrue();
		assertThat(responseJson.getAsJsonArray().get(0).getAsString()).isEqualTo("1");
		assertThat(responseJson.getAsJsonArray().get(1).getAsString()).isEqualTo("2");
		assertThat(responseJson.getAsJsonArray().size()).isEqualTo(2);

	}

	@Test
	public void testGetJsonElement() {

		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive("1"));
		when(resource.getJsonElement()).thenReturn(json);

		ClientResponse response = RULE.client().resource("/test/ele").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonObject()).isTrue();
		assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

	}

	@Test(expected = ConstraintViolationException.class)
	public void testPostMalformedJsonObject() {
		RULE.client().resource("/test/obj").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, "{\"a\"1\"}");
	}

	@Test
	public void testPostNullJsonObject() {
		RULE.client().resource("/test/obj").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, "");

		ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
		verify(resource).postJsonObject(captor.capture());
		assertThat(captor.getValue()).isNull();
	}

	@Test
	public void testPostJsonObject() {
		RULE.client().resource("/test/obj").type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, "{\"a\":\"1\"}");

		ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
		verify(resource, atLeastOnce()).postJsonObject(captor.capture());
		assertThat(captor.getValue().get("a").getAsString()).isEqualTo("1");
	}

	@Path("/test")
	public static interface TestResource {

		@GET
		@Path("/objRes")
		public Response getJsonObjectResponse();

		@GET
		@Path("/obj")
		public JsonObject getJsonObject();

		@GET
		@Path("/arrayRes")
		public Response getJsonArrayResponse();

		@GET
		@Path("/array")
		public JsonArray getJsonArray();

		@GET
		@Path("/ele")
		public JsonElement getJsonElement();

		@POST
		@Path("/obj")
		public Response postJsonObject(JsonObject obj);
	}

}
