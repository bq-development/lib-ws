/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.json.serialization;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Period;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Alexander De Leon
 *
 */
public class PeriodJsonDeserializerTest {

	@Test
	public void testNullPeriod() throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"period\":null}", TestBean.class);
		assertThat(bean.period).isNull();
	}

	@Test
	public void testMissinPeriod() throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"x\":\"x\"}", TestBean.class);
		assertThat(bean.period).isNull();
	}

	@Test
	public void testPeriod() throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"period\":\"P1y2m5d\"}", TestBean.class);
		assertThat(bean.period.getYears()).isEqualTo(1);
		assertThat(bean.period.getMonths()).isEqualTo(2);
		assertThat(bean.period.getDays()).isEqualTo(5);
	}

	public static class TestBean {
		private String x;
		@JsonDeserialize(using = PeriodJsonDeserializer.class)
		private Period period;

		public String getX() {
			return x;
		}

		public void setX(String x) {
			this.x = x;
		}

		public Period getPeriod() {
			return period;
		}

		public void setPeriod(Period period) {
			this.period = period;
		}

	}
}
