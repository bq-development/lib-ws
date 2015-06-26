/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.gson;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * @author Alexander De Leon
 * 
 */
public class GsonMessageReaderWriterProviderTest {

    private static final GsonMessageReaderWriterProvider provider = spy(new GsonMessageReaderWriterProvider());
    private static final TestResource resource = mock(TestResource.class);

    @ClassRule public static final ResourceTestRule RULE = ResourceTestRule.builder().addResource(resource).addProvider(provider).build();

    @Test
    public void testGetJsonObjectResponse() {

        JsonObject json = new JsonObject();
        json.add("a", new JsonPrimitive("1"));
        when(resource.getJsonObjectResponse()).thenReturn(Response.ok().entity(json).build());

        Response response = RULE.client().target("/test/objRes").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        JsonElement responseJson = new JsonParser().parse(response.readEntity(String.class));
        assertThat(responseJson.isJsonObject()).isTrue();
        assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

    }

    @Test
    public void testGetJsonObjectResponseEmpty() {

        when(resource.getJsonObjectResponse()).thenReturn(Response.ok().build());

        Response response = RULE.client().target("/test/objRes").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        assertThat(response.readEntity(String.class)).isEqualTo("");
    }

    @Test
    public void testGetJsonObjectResponseNull() {

        when(resource.getJsonObjectResponse()).thenReturn(Response.ok().entity(null).build());

        Response response = RULE.client().target("/test/objRes").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        assertThat(response.readEntity(String.class)).isEqualTo("");
    }

    @Test
    public void testGetJsonObject() {

        JsonObject json = new JsonObject();
        json.add("a", new JsonPrimitive("1"));
        when(resource.getJsonObject()).thenReturn(json);

        Response response = RULE.client().target("/test/obj").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        JsonElement responseJson = new JsonParser().parse(response.readEntity(String.class));
        assertThat(responseJson.isJsonObject()).isTrue();
        assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

    }

    @Test
    public void testGetJsonArrayResponse() {

        JsonArray json = new JsonArray();
        json.add(new JsonPrimitive("1"));
        json.add(new JsonPrimitive("2"));
        when(resource.getJsonArrayResponse()).thenReturn(Response.ok().entity(json).build());

        Response response = RULE.client().target("/test/arrayRes").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        JsonElement responseJson = new JsonParser().parse(response.readEntity(String.class));
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

        Response response = RULE.client().target("/test/array").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        JsonElement responseJson = new JsonParser().parse(response.readEntity(String.class));
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

        Response response = RULE.client().target("/test/ele").request(MediaType.APPLICATION_JSON_TYPE).get(Response.class);

        JsonElement responseJson = new JsonParser().parse(response.readEntity(String.class));
        assertThat(responseJson.isJsonObject()).isTrue();
        assertThat(responseJson.getAsJsonObject().get("a").getAsString()).isEqualTo("1");

    }

    @Test(expected = ConstraintViolationException.class)
    public void testPostMalformedJsonObject() throws Throwable {
        try {
            RULE.client().target("/test/obj").request().header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.json("{\"a\"1\"}"), Response.class);
        } catch (ProcessingException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testPostNullJsonObject() {
        RULE.client().target("/test/obj").request().header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(""), Response.class);

        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        verify(resource).postJsonObject(captor.capture());
        assertThat(captor.getValue()).isNull();
    }

    @Test
    public void testPostJsonObject() {
        RULE.client().target("/test/obj").request().header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json("{\"a\":\"1\"}"), Response.class);

        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        verify(resource, atLeastOnce()).postJsonObject(captor.capture());
        assertThat(captor.getValue().get("a").getAsString()).isEqualTo("1");
    }

    @Path("/test") public static interface TestResource {

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
