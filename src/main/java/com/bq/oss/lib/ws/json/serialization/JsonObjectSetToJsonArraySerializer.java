package com.bq.oss.lib.ws.json.serialization;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.JsonObject;

public class JsonObjectSetToJsonArraySerializer extends JsonSerializer<Set<JsonObject>> {

    @Override
    public void serialize(Set<JsonObject> jsonObjects, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeRawValue(jsonObjects.toString());
    }
}