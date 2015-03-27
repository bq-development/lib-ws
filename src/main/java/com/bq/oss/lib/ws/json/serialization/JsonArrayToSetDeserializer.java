package com.bq.oss.lib.ws.json.serialization;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Alberto J. Rubio
 */
public class JsonArrayToSetDeserializer extends JsonDeserializer<Set<JsonObject>> {
    com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

    @Override
    public Set<JsonObject> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode tree = jp.getCodec().readTree(jp);
        JsonArray elements = parser.parse(tree.toString()).getAsJsonArray();
        Set<JsonObject> objects = new HashSet<>(elements.size());
        for(JsonElement element : elements) {
            objects.add(element.getAsJsonObject());
        }
        return objects;
    }
}