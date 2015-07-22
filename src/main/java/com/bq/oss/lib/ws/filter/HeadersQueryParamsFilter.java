package com.bq.oss.lib.ws.filter;

import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Francisco Sanchez on 29/10/14.
 */
@PreMatching @Priority(1) public class HeadersQueryParamsFilter extends OptionalContainerRequestFilter {

    private static final String HEADER_KEY = "headers";
    private final ObjectMapper objectMapper;
    private final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};

    private static final Logger LOG = LoggerFactory.getLogger(HeadersQueryParamsFilter.class);

    public HeadersQueryParamsFilter(boolean enabled, ObjectMapper objectMapper) {
        super(enabled);
        this.objectMapper = objectMapper;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        String headerQueryParameters = request.getUriInfo().getQueryParameters().getFirst(HEADER_KEY);
        if (null != headerQueryParameters) {
            try {
                HashMap<String, String> queryParamHeaders = objectMapper.readValue(headerQueryParameters, typeRef);
                for (String key : queryParamHeaders.keySet()) {
                    try {
                        request.getHeaders().putSingle(key, URLDecoder.decode(queryParamHeaders.get(key), "UTF-8"));
                    } catch (UnsupportedEncodingException ignored) {
                        LOG.error("Cannot decode query param value: " + queryParamHeaders.get(key));
                    }
                }
            } catch (IOException e) {
                throw new WebApplicationException(ErrorResponseFactory.getInstance().badRequest(
                        new com.bq.oss.lib.ws.model.Error("bad_request", "invalid query parameter format: " + HEADER_KEY)));
            }
        }
    }
}
