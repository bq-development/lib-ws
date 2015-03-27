package com.bq.oss.lib.ws.json.serialization;

import java.io.IOException;
import java.time.temporal.TemporalAmount;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TemporalAmountJsonSerializer extends JsonSerializer<TemporalAmount> {


    @Override
    public void serialize(TemporalAmount value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toString());
    }
}