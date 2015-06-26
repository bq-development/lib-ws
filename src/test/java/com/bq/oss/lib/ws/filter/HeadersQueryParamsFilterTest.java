/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.filter;


import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * @author Francisco Sanchez
 */

@RunWith(MockitoJUnitRunner.class) public class HeadersQueryParamsFilterTest {

    private static final String OBJ_1 = "{\"test\": \"test\"}";
    private static final Map<String, String> PARSED_OBJ_1 = ImmutableMap.of("test", "test");
    private static final String OBJ_2 = "BAD_JSON";

    private HeadersQueryParamsFilter filter;

    @Mock private ContainerRequestContext requestMock;
    private ObjectMapper objectMapper;
    private MultivaluedHashMap queryParameters;

    @Before
    public void setup() throws IOException {
        objectMapper = new ObjectMapper();
        MultivaluedMap headers = new MultivaluedHashMap();
        when(requestMock.getHeaders()).thenReturn(headers);
        queryParameters = new MultivaluedHashMap();
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
        when(requestMock.getUriInfo()).thenReturn(uriInfo);
        filter = new HeadersQueryParamsFilter(true, objectMapper);
    }

    @Test
    public void testParseJson() {
        queryParameters.add("headers", OBJ_1);
        filter.filter(requestMock);
        PARSED_OBJ_1.keySet().stream().forEach(key -> assertThat(requestMock.getHeaders().getFirst(key)).isEqualTo(PARSED_OBJ_1.get(key)));
    }

    @Test(expected = WebApplicationException.class)
    public void testParseBadJson() {
        queryParameters.add("headers", OBJ_2);
        filter.filter(requestMock);
    }

    @Test
    public void testFilterDisabled() {
        filter = new HeadersQueryParamsFilter(false, null);
        assertThat(filter.isEnabled()).isFalse();
    }
}
