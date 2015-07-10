package com.bq.oss.lib.ws.filter;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;

import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Francisco Sanchez on 29/10/14.
 */
@Priority(Priorities.HEADER_DECORATOR) public class HeadersQueryParamsFilter extends OptionalContainerRequestFilter {
    private static final String HEADER_KEY = "headers";
    private final ObjectMapper objectMapper;
    private final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};

    public HeadersQueryParamsFilter(boolean enabled, ObjectMapper objectMapper) {
        super(enabled);
        this.objectMapper = objectMapper;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        String headerQueryParameters = request.getUriInfo().getQueryParameters().getFirst(HEADER_KEY);
        if (null != headerQueryParameters) {
            try {
                replaceHeaders(request.getHeaders(), objectMapper.readValue(headerQueryParameters, typeRef));
            } catch (IOException e) {
                throw new WebApplicationException(ErrorResponseFactory.getInstance().badRequest(
                        new com.bq.oss.lib.ws.model.Error("bad_request", "invalid query parameter format: " + HEADER_KEY)));
            }
        }
    }

    private void replaceHeaders(MultivaluedMap<String, String> headers, HashMap<String, String> queryParamHeaders) {
        queryParamHeaders.keySet().forEach(key -> headers.putSingle(key, queryParamHeaders.get(key)));
    }


}
