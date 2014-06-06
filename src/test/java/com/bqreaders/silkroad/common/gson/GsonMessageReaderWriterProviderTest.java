/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.gson;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;
import com.yammer.dropwizard.validation.InvalidEntityException;

/**
 * @author Alexander De Leon
 * 
 */
public class GsonMessageReaderWriterProviderTest extends ResourceTest {

	private GsonMessageReaderWriterProvider provider;
	private TestResource resource;

	@Override
	protected void setUpResources() throws Exception {
		provider = spy(new GsonMessageReaderWriterProvider());
		addProvider(provider);

		resource = mock(TestResource.class);
		addResource(resource);
	}

	@Test
	public void testGetJsonObjectResponse() {

		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive("1"));
		when(resource.getJsonObjectResponse()).thenReturn(Response.ok().entity(json).build());

		ClientResponse response = client().resource("/test/objRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonObject()).isTrue();
		assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

	}

	@Test
	public void testGetJsonObjectResponseEmpty() {

		when(resource.getJsonObjectResponse()).thenReturn(Response.ok().build());

		ClientResponse response = client().resource("/test/objRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		assertThat(response.getEntity(String.class)).isEqualTo("");
	}

	@Test
	public void testGetJsonObjectResponseNull() {

		when(resource.getJsonObjectResponse()).thenReturn(Response.ok().entity(null).build());

		ClientResponse response = client().resource("/test/objRes").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		assertThat(response.getEntity(String.class)).isEqualTo("");
	}

	@Test
	public void testGetJsonObject() {

		JsonObject json = new JsonObject();
		json.add("a", new JsonPrimitive("1"));
		when(resource.getJsonObject()).thenReturn(json);

		ClientResponse response = client().resource("/test/obj").accept(MediaType.APPLICATION_JSON_TYPE)
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

		ClientResponse response = client().resource("/test/arrayRes").accept(MediaType.APPLICATION_JSON_TYPE)
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

		ClientResponse response = client().resource("/test/array").accept(MediaType.APPLICATION_JSON_TYPE)
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

		ClientResponse response = client().resource("/test/ele").accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		JsonElement responseJson = new JsonParser().parse(response.getEntity(String.class));
		assertThat(responseJson.isJsonObject()).isTrue();
		assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

	}

	@Test
	public void testPostJsonObject() {
		client().resource("/test/obj").type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, "{\"a\":\"1\"}");

		ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
		verify(resource).postJsonObject(captor.capture());
		assertThat(captor.getValue().get("a").getAsString()).isEqualTo("1");
	}

	@Test(expected = InvalidEntityException.class)
	public void testPostMalformedJsonObject() {
		client().resource("/test/obj").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, "{\"a\":1\"}");
	}

	@Test
	public void testPostNullJsonObject() {
		client().resource("/test/obj").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, "");

		ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
		verify(resource).postJsonObject(captor.capture());
		assertThat(captor.getValue()).isNull();
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
