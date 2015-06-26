/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.glassfish.jersey.message.internal.ReaderWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Jax-rs {@link javax.ws.rs.ext.Provider} for Gson objects.
 * 
 * @author Alexander De Leon
 * 
 */
public class GsonMessageReaderWriterProvider implements MessageBodyReader<JsonElement>, MessageBodyWriter<JsonElement> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (isJsonType(mediaType)) {
            return JsonElement.class.isAssignableFrom(type);
        }
        return false;
    }

    @Override
    public long getSize(JsonElement t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return t.toString().length();
    }

    @Override
    public JsonElement readFrom(Class<JsonElement> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        if (entityStream == null) {
            return null;
        }
        JsonParser parser = new JsonParser();
        try {

            JsonElement element = parser.parse(ReaderWriter.readFromAsString(entityStream, mediaType));
            return element.isJsonNull() ? null : assertType(element, type);
        } catch (JsonParseException e) {
            throw new ConstraintViolationException("Malformed JSON:" + e.getMessage(), Collections.emptySet());
        }
    }

    private JsonElement assertType(JsonElement element, Class<JsonElement> type) {
        if (type.isAssignableFrom(element.getClass())) {
            return element;
        }
        throw new ConstraintViolationException("Malformed JSON: " + "Expecting " + type + " but received " + element.getClass(),
                Collections.emptySet());
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
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        if (t != null) {
            ReaderWriter.writeToAsString(t.toString(), entityStream, mediaType);
        }
    }

    /*
     * 
     * Taken from com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
     */
    protected boolean isJsonType(MediaType mediaType) {
        /*
         * As suggested by Stephen D, there are 2 ways to check: either being as inclusive as possible (if subtype is "json"), or exclusive
         * (major type "application", minor type "json"). Let's start with inclusive one, hard to know which major types we should cover
         * aside from "application".
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
