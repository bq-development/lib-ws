package io.corbel.lib.ws.json.serialization;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.time.temporal.TemporalAmount;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Alberto J. Rubio
 *
 */
public class TemporalAmountJsonSerializerTest {

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

    @Test
    public void testDuration() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{\"period\":\"PT15M\"}", TestBean.class);
        assertThat(bean.getPeriod().toString()).isEqualTo("PT15M");
    }

	public static class TestBean {
		private String x;

		@JsonDeserialize(using = TemporalAmountJsonDeserializer.class)
		private TemporalAmount period;

		public String getX() {
			return x;
		}

		public void setX(String x) {
			this.x = x;
		}

		@JsonSerialize(using = TemporalAmountJsonSerializer.class)
		public TemporalAmount getPeriod() {
			return period;
		}

		public void setPeriod(TemporalAmount period) {
			this.period = period;
		}

	}
}
