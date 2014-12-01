/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Jax-rs {@link javax.ws.rs.ext.Provider} for Gson objects.
 * 
 * @author Alexander De Leon
 * 
 */
public class GsonMessageReaderWriterProvider extends AbstractMessageReaderWriterProvider<JsonElement> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (isJsonType(mediaType)) {
			return JsonElement.class.isAssignableFrom(type);
		}
		return false;
	}

	@Override
	public JsonElement readFrom(Class<JsonElement> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		if (entityStream == null) {
			return null;
		}
		JsonParser parser = new JsonParser();
		try {

			JsonElement element = parser.parse(super.readFromAsString(entityStream, mediaType));
			return element.isJsonNull() ? null : assertType(element, type);
		} catch (JsonParseException e) {
			throw new ConstraintViolationException("Malformed JSON:" + e.getMessage(), Collections.emptySet());
		}
	}

	private JsonElement assertType(JsonElement element, Class<JsonElement> type) {
		if (type.isAssignableFrom(element.getClass())) {
			return element;
		}
		throw new ConstraintViolationException("Malformed JSON: " + "Expecting " + type + " but received "
				+ element.getClass(), Collections.emptySet());
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (isJsonType(mediaType)) {
			return JsonElement.class.isAssignableFrom(type);
		}
		return false;
	}

	@Override
	public void writeTo(JsonElement t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
			WebApplicationException {
		if (t != null) {
			super.writeToAsString(t.toString(), entityStream, mediaType);
		}
	}

	/*
	 * 
	 * Taken from com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
	 */
	protected boolean isJsonType(MediaType mediaType) {
		/*
		 * As suggested by Stephen D, there are 2 ways to check: either being as inclusive as possible (if subtype is
		 * "json"), or exclusive (major type "application", minor type "json"). Let's start with inclusive one, hard to
		 * know which major types we should cover aside from "application".
		 */
		if (mediaType != null) {
			// Ok: there are also "xxx+json" subtypes, which count as well
			String subtype = mediaType.getSubtype();
			return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json");
		}
		/*
		 * Not sure if this can happen; but it seems reasonable that we can at least produce json without media type?
		 */
		return true;
	}

}
