package com.bqreaders.silkroad.common.filter;

import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

import com.bqreaders.silkroad.common.api.error.ErrorResponseFactory;
import com.bqreaders.silkroad.common.model.Error;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Created by Francisco Sanchez on 29/10/14.
 */
public class HeadersQueryParamsFilter implements ContainerRequestFilter {
	private static final String HEADER_KEY = "headers";
	private final boolean enabled;
	private final ObjectMapper objectMapper;
	private final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
	};

	public HeadersQueryParamsFilter(boolean enabled, ObjectMapper objectMapper) {
		this.enabled = enabled;
		this.objectMapper = objectMapper;

	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		if (enabled) {
			String headerQueryParameters = request.getQueryParameters().getFirst(HEADER_KEY);
			if (null != headerQueryParameters) {
				try {
					replaceHeaders(request.getRequestHeaders(), objectMapper.readValue(headerQueryParameters, typeRef));
				} catch (IOException e) {
					throw new WebApplicationException(ErrorResponseFactory.getInstance().badRequest(
							new Error("bad_request", "invalid query parameter format: " + HEADER_KEY)));
				}
			}
		}
		return request;
	}

	private void replaceHeaders(MultivaluedMap<String, String> headers, HashMap<String, String> queryParamHeaders ) {
        queryParamHeaders.keySet().forEach(key -> headers.putSingle(key, queryParamHeaders.get(key)));
    }
}
