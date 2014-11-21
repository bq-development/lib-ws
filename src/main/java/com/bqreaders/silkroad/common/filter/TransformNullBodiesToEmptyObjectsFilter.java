package com.bqreaders.silkroad.common.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class TransformNullBodiesToEmptyObjectsFilter implements ContainerRequestFilter {

	private static final String EMPTY_JSON_OBJECT = "{}";
	private static final Logger LOG = LoggerFactory.getLogger(TransformNullBodiesToEmptyObjectsFilter.class);

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		MediaType mediaType = request.getMediaType();
		if (mediaType==null)  {
			return request;
		}
		mediaType = new MediaType(mediaType.getType(), mediaType.getSubtype());
		String method = request.getMethod();
		boolean isMethodPostOrPut = ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method));
		boolean isJSON = (MediaType.APPLICATION_JSON_TYPE.equals(mediaType));
		try {
			boolean isEntityEmpty = (request.getEntityInputStream().available() == 0);

			if (isMethodPostOrPut && isJSON && isEntityEmpty) {
				LOG.info(
						"Got an empty entity body with method={} and mediaType={}. Will inject {} into the request",
						method, mediaType, EMPTY_JSON_OBJECT);
				if (request.getEntityInputStream() != null) {
					request.getEntityInputStream().close();
				}
				InputStream is = new ByteArrayInputStream(EMPTY_JSON_OBJECT.getBytes());
				request.setEntityInputStream(is);
				LOG.info("Successfully injected {} into the request", EMPTY_JSON_OBJECT);
			}
		} catch (IOException e) {
			LOG.error("Caught exception while trying to inject an empty JSON object into the request: {}\n.",
					e.getMessage());
			LOG.error(
					"Will proceed with method={},mediaType={} and empty entity body. This may cause some problems in a second",
					method, mediaType);
		}
		return request;
	}
}