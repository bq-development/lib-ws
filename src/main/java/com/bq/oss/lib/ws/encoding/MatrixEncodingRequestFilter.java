/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.encoding;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * A workaround to encode slash (/) in matrix params
 * 
 * @author Alexander De Leon
 * 
 */
public class MatrixEncodingRequestFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(MatrixEncodingRequestFilter.class);

	private final Pattern matrixPattern;

	/**
	 * Creates a new filter for the specified pattern
	 * 
	 * @param matrixPattern
	 *            A pattern according to the {@link java.util.regex.Pattern} class. This pattern must contain two groups. The first
	 *            group the non placing part of the path and the second the replacing part. For example:
	 *            ^(/v1.0/resource/.+/.+/.+;r=)(.+)$
	 */
	public MatrixEncodingRequestFilter(String matrixPattern) {
		this.matrixPattern = Pattern.compile(matrixPattern);
		LOG.info("Creating matrix encoding filter for path pattern {}", matrixPattern);
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		URI path = request.getAbsolutePath();
		Matcher matcher = matrixPattern.matcher(path.toASCIIString());
		if (matcher.matches()) {
			String encodedMatrix = matcher.group(2).replaceAll("/", "%2F");
			String encodedRequestUri = matcher.replaceFirst("$1" + encodedMatrix);
			request.setUris(request.getBaseUri(), URI.create(encodedRequestUri));
		}
		return request;
	}
}
