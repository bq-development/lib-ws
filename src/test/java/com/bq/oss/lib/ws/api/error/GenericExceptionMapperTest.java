/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.api.error;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author Alexander De Leon
 * 
 */
public class GenericExceptionMapperTest {

    private static final TestResource resourceMock = mock(TestResource.class);

    @ClassRule public static final ResourceTestRule RULE = ResourceTestRule.builder().addResource(resourceMock)
            .addProvider(GenericExceptionMapper.class).build();

    @Test
    public void testUnknowException() {
        Mockito.doThrow(new RuntimeException("test")).when(resourceMock).get();
        Response response = RULE.client().target("/test").request().get();
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getMediaType()).isEqualTo(MediaType.APPLICATION_JSON_TYPE);
        assertThat(response.readEntity(String.class)).contains("\"error\":\"internal_server_error\"");

    }

    @Test
    public void testWebApplicationException() {
        Mockito.doThrow(new WebApplicationException(Response.ok("ok").build())).when(resourceMock).get();
        Response response = RULE.client().target("/test").request().get(Response.class);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("ok");

    }

    @Path("/test") public interface TestResource {
        @GET
        Response get();
    }

}
