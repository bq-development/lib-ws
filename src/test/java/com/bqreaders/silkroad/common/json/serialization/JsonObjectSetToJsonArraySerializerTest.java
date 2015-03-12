package com.bqreaders.silkroad.common.json.serialization;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.JsonObject;

public class JsonObjectSetToJsonArraySerializerTest {
    public static class TestClass {
        @JsonDeserialize(using = JsonArrayToSetDeserializer.class)
        private Set<JsonObject> jsonObjects;

        @JsonSerialize(using = JsonObjectSetToJsonArraySerializer.class)
        public Set<JsonObject> getJsonObjects() {
            return jsonObjects;
        }

        public void setJsonObjects(Set<JsonObject> jsonObjects) {
            this.jsonObjects = jsonObjects;
        }
    }

    @Test
    public void testSerialize() throws JsonProcessingException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("testProperty", 23);

        Set<JsonObject> jsonObjects = new HashSet<>();
        jsonObjects.add(jsonObject);

        TestClass testClass = new TestClass();
        testClass.setJsonObjects(jsonObjects);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAsString = objectMapper.writeValueAsString(testClass);

        assertEquals("{\"jsonObjects\":[{\"testProperty\":23}]}", jsonAsString);
    }
}
