package io.corbel.lib.ws.json.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import static org.fest.assertions.api.Assertions.assertThat;


public class LocalDateTimeJsonDeserializerTest {

    @Test
    public void testNullLocalDateTime() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{\"moment\":null}", TestBean.class);
        assertThat(bean.getMoment()).isNull();
    }

    @Test
    public void testMissingLocalDateTime() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{}", TestBean.class);
        assertThat(bean.getMoment()).isNull();
    }

    @Test
    public void testLocalDateTimeWithoutDate() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{\"moment\":\"06:42\"}", TestBean.class);
        assertThat(((LocalDateTime) bean.getMoment()).getHour()).isEqualTo(6);
        assertThat(((LocalDateTime) bean.getMoment()).getMinute()).isEqualTo(42);
        assertThat(((LocalDateTime) bean.getMoment()).getYear()).isEqualTo(DateTime.now().getYear());
        assertThat(((LocalDateTime) bean.getMoment()).getDayOfYear()).isEqualTo(DateTime.now().getDayOfYear());
        assertThat(((LocalDateTime) bean.getMoment()).getMonthValue()).isEqualTo(DateTime.now().getMonthOfYear());
    }

    @Test
    public void testLocalDateTimeWithDate() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestBean bean = mapper.readValue("{\"moment\":\"2007-12-03T10:15:30\"}", TestBean.class);
        assertThat(((LocalDateTime) bean.getMoment()).getHour()).isEqualTo(10);
        assertThat(((LocalDateTime) bean.getMoment()).getMinute()).isEqualTo(15);
        assertThat(((LocalDateTime) bean.getMoment()).getYear()).isEqualTo(2007);
        assertThat(((LocalDateTime) bean.getMoment()).getDayOfYear()).isEqualTo(337);
        assertThat(((LocalDateTime) bean.getMoment()).getMonthValue()).isEqualTo(12);
    }


    public static class TestBean {

        private String x;
        @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class) private LocalDateTime moment;

        private LocalDateTime getMoment() {
            return moment;
        }
    }
}
