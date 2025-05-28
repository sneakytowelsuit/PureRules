package com.github.sneakytowelsuit.purerules;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class RuleSerializer extends StdSerializer<Rule<?, ?>> {
    private static final String JSON_FIELD_NAME = "field";
    private static final String JSON_OPERATOR_NAME  = "operator";
    private static final String JSON_VALUE_NAME = "value";
    public RuleSerializer() {
        this((Class<Rule<?, ?>>) Rule.builder().build().getClass());
    }

    private RuleSerializer(Class<Rule<?, ?>> t) {
        super(t);
    }

    @Override
    public void serialize(Rule<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField(JSON_FIELD_NAME, value.getField().getClass().getName());
        gen.writeStringField(JSON_OPERATOR_NAME, value.getOperator().getClass().getName());
        gen.writePOJOField(JSON_VALUE_NAME, value.getValue());
        gen.writeEndObject();
    }
}
