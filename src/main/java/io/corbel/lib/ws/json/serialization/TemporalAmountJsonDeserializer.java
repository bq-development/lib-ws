package io.corbel.lib.ws.json.serialization;

import java.io.IOException;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TemporalAmountJsonDeserializer extends JsonDeserializer<TemporalAmount> {

	@Override
	public TemporalAmount deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		try {
			return Period.parse(jp.getText());
		} catch (Exception e) {
			return Duration.parse(jp.getText());
		}
	}

}