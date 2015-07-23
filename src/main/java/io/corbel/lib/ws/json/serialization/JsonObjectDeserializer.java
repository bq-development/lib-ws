package io.corbel.lib.ws.json.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * @author Alberto J. Rubio
 */
public class JsonObjectDeserializer extends JsonDeserializer<JsonObject> {
    com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

    @Override
    public JsonObject deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode tree = jp.getCodec().readTree(jp);
        return parser.parse(tree.toString()).getAsJsonObject();
    }
}