package com.github.sneakytowelsuit.purerules.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.sneakytowelsuit.purerules.conditions.Rule;

import java.io.IOException;

public class RuleSerializer extends StdSerializer<Rule<?, ?>> {
    public RuleSerializer() {
        this((Class<Rule<?, ?>>) Rule.builder().build().getClass());
    }

    private RuleSerializer(Class<Rule<?, ?>> t) {
        super(t);
    }

    @Override
    public void serialize(Rule<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField(RuleGroupJsonKeys.FIELD.getKey(), value.getField().getClass().getName());
        gen.writeStringField(RuleGroupJsonKeys.OPERATOR.getKey(), value.getOperator().getClass().getName());
        gen.writeFieldName(RuleGroupJsonKeys.VALUE.getKey());
        gen.writeStartObject();
        gen.writeStringField(RuleGroupJsonKeys.VALUE_CLASS.getKey(), value.getValue().getClass().getName());
        gen.writePOJOField(RuleGroupJsonKeys.VALUE_VALUE.getKey(), value.getValue());
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
