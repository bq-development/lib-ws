/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.json.serialization;

import java.io.IOException;
import java.time.Period;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author Rubén Carrasco
 * 
 */
public class PeriodJsonDeserializer extends JsonDeserializer<Period> {

	@Override
	public Period deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return Period.parse(jp.getText());
	}

}
