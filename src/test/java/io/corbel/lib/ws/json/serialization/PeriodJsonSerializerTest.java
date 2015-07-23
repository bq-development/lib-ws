package io.corbel.lib.ws.json.serialization;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Period;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Alexander De Leon
 *
 */
public class PeriodJsonSerializerTest {

	@Test
	public void testNullPeriod() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"period\":null}", TestBean.class);
		assertThat(bean.getPeriod()).isNull();
	}

	@Test
	public void testMissingPeriod() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"x\":\"x\"}", TestBean.class);
		assertThat(bean.getPeriod()).isNull();
	}

	@Test
	public void testPeriod() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		TestBean bean = mapper.readValue("{\"period\":\"P1Y2M5D\"}", TestBean.class);
		assertThat(bean.getPeriod().toString()).isEqualTo("P1Y2M5D");
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

		@JsonSerialize(using = PeriodJsonSerializer.class)
		public Period getPeriod() {
			return period;
		}

		public void setPeriod(Period period) {
			this.period = period;
		}

	}
}
