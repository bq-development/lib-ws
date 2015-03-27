/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.json.serialization;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.JsonObject;

/**
 * @author Alberto J. Rubio
 *
 */
public class JsonArrayToSetDeserializerTest {

    private static final String TEST_OBJECT= "[{\"id\":\"95\",\"type\":\"SR\"}, {\"id\":\"48\",\"type\":\"CB\"}]";

	@Test
	public void testNullObject() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"objects\": null}", TestBean.class);
		assertThat(bean.objects).isNull();
	}

	@Test
	public void testMissingObject() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"x\":\"x\"}", TestBean.class);
		assertThat(bean.objects).isNull();
	}

    @Test
    public void testObject() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{\"objects\": " + TEST_OBJECT + "}", TestBean.class);
        assertThat(bean.objects.toString()).isEqualTo(TEST_OBJECT);
    }

	public static class TestBean {

        private String x;

        @JsonDeserialize(using = JsonArrayToSetDeserializer.class)
        private Set<JsonObject> objects;


        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public Set<JsonObject> getObjects() {
            return objects;
        }

        public void setObjects(Set<JsonObject> objects) {
            this.objects = objects;
        }
    }
}
