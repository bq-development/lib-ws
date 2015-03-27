/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.json.serialization;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import com.google.gson.JsonObject;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Alberto J. Rubio
 *
 */
public class JsonObjectDeserializerTest {

    private static final String TEST_OBJECT= "{\"id\":\"95\",\"type\":\"SR\"}";

	@Test
	public void testNullObject() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"object\":null}", TestBean.class);
		assertThat(bean.object).isNull();
	}

	@Test
	public void testMissingObject() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"x\":\"x\"}", TestBean.class);
		assertThat(bean.object).isNull();
	}

    @Test
    public void testObject() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{\"object\": " + TEST_OBJECT + "}", TestBean.class);
        assertThat(bean.object.toString()).isEqualTo(TEST_OBJECT);
    }

	public static class TestBean {

        private String x;

        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject object;


        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public JsonObject getObject() {
            return object;
        }

        public void setObject(JsonObject object) {
            this.object = object;
        }
    }
}
