/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.cors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.HttpMethod;

import com.bq.oss.lib.ws.model.CustomHeaders;
import com.google.common.base.Joiner;
import com.google.common.net.HttpHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * This filter permits different configurations for the CORS behaviour.
 *
 * @see <a href="http://www.w3.org/TR/cors/">http://www.w3.org/TR/cors/</a>
 *
 * @author Alexander De Leon
 * 
 */
public class CorsResponseFilter implements ContainerResponseFilter {

	private static final String SPACE = " ";

	private final List<String> allowedOrigins;
	private final int preflightRequestMaxAge;

	public static CorsResponseFilter disabled() {
		return new CorsResponseFilter(Collections.<String> emptyList(), 0);
	}

	public static CorsResponseFilter anyOrigin(int preflightRequestMaxAge) {
		return new CorsResponseFilter(null, preflightRequestMaxAge);
	}

	public static CorsResponseFilter onlyAllowedOrigins(int preflightRequestMaxAge, String... allowedOrigins) {
		return new CorsResponseFilter(Arrays.asList(allowedOrigins), preflightRequestMaxAge);
	}

	public CorsResponseFilter(List<String> allowedOrigins, int preflightRequestMaxAge) {
		this.allowedOrigins = allowedOrigins;
		this.preflightRequestMaxAge = preflightRequestMaxAge;
	}

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		String origin = allowAnyOrigin() ? request.getHeaderValue(HttpHeaders.ORIGIN) : serializedOriginList();
		if (origin != null) {
			response.getHttpHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
			response.getHttpHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			if (request.getMethod().equals(HttpMethod.OPTIONS)) {
				response.getHttpHeaders().add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, preflightRequestMaxAge);
				response.getHttpHeaders().add(
						HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
						Joiner.on(",").join(HttpHeaders.AUTHORIZATION, HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE,
								CustomHeaders.NO_REDIRECT_HEADER.toString(),
								CustomHeaders.REQUEST_COOKIE_HEADER.toString()));
				response.getHttpHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
						Joiner.on(",").join(HttpHeaders.LOCATION, HttpHeaders.DATE));
				if (response.getHttpHeaders().containsKey(HttpHeaders.ALLOW)) {
					response.getHttpHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
							response.getHttpHeaders().getFirst(HttpHeaders.ALLOW));
				}
			} else if (response.getHttpHeaders().containsKey(HttpHeaders.LOCATION)) {
				response.getHttpHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION);
			}

		}
		return response;
	}

	private boolean allowAnyOrigin() {
		return allowedOrigins == null;
	}

	private String serializedOriginList() {
		return allowedOrigins.isEmpty() ? null : Joiner.on(SPACE).join(allowedOrigins);
	}

}
