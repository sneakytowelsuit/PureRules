package com.github.sneakytowelsuit.rule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class RuleGroupSerializer extends StdSerializer<RuleGroup<?>> {
    private static final String INVERTED_FIELD_NAME = "inverted";
    private static final String COMBINATOR_FIELD_NAME = "combinator";
    private static final String BIAS_FIELD_NAME = "bias";
    public RuleGroupSerializer() {
        this((Class<RuleGroup<?>>) RuleGroup.builder().build().getClass());
    }

    private RuleGroupSerializer(Class<RuleGroup<?>> t) {
        super(t);
    }

    @Override
    public void serialize(RuleGroup<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField(INVERTED_FIELD_NAME, String.valueOf(value.isInverted()));
        gen.writeStringField(COMBINATOR_FIELD_NAME, value.getCombinator().name());
        gen.writeStringField(BIAS_FIELD_NAME, value.getBias().name());
        gen.writeArrayFieldStart("conditions");
        value.getConditions().forEach(condition -> {
            switch(condition) {
                case Rule rule -> {
                    try {
                        provider.findValueSerializer(Rule.class).serialize(condition, gen, provider);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case RuleGroup ruleGroup -> {
                    try {
                        provider.findValueSerializer(RuleGroup.class).serialize(condition, gen, provider);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
