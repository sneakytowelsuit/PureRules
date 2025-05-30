package com.github.sneakytowelsuit.purerules.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;

import java.io.IOException;

public class RuleGroupSerializer extends StdSerializer<RuleGroup<?>> {
    public RuleGroupSerializer() {
        this((Class<RuleGroup<?>>) RuleGroup.builder().build().getClass());
    }

    private RuleGroupSerializer(Class<RuleGroup<?>> t) {
        super(t);
    }

    @Override
    public void serialize(RuleGroup<?> value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField(RuleGroupJsonKeys.INVERTED.getKey(), String.valueOf(value.isInverted()));
        gen.writeStringField(RuleGroupJsonKeys.COMBINATOR.getKey(), value.getCombinator().name());
        gen.writeStringField(RuleGroupJsonKeys.BIAS.getKey(), value.getBias().name());
        gen.writeArrayFieldStart(RuleGroupJsonKeys.CONDITIONS.getKey());
        value.getConditions().forEach(condition -> {
            switch(condition) {
                case Rule rule -> {
                    try {
                        provider.findValueSerializer(Rule.class).serialize(rule, gen, provider);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case RuleGroup ruleGroup -> {
                    try {
                        provider.findValueSerializer(RuleGroup.class).serialize(ruleGroup, gen, provider);
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
