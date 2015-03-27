package com.bq.oss.lib.ws.json.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.JsonObject;

public class JsonObjectSerializerTest {

    public static class TestClass {
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject jsonObject;

        @JsonSerialize(using = JsonObjectSerializer.class)
        public JsonObject getJsonObject() {
            return jsonObject;
        }

        public void setJsonObject(JsonObject jsonObject) {
            this.jsonObject = jsonObject;
        }
    }

    @Test
    public void testSerialize() throws JsonProcessingException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "John Doe");

        TestClass testClass = new TestClass();
        testClass.setJsonObject(jsonObject);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAsString = objectMapper.writeValueAsString(testClass);

        assertEquals("{\"jsonObject\":{\"name\":\"John Doe\"}}", jsonAsString);
    }
}
