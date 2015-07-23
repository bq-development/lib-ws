package io.corbel.lib.ws.json.serialization;

import java.io.IOException;
import java.time.Period;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class PeriodJsonDeserializer extends JsonDeserializer<Period> {

	@Override
	public Period deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		return Period.parse(jp.getText());
	}

}
