package com.github.sneakytowelsuit.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class RuleGroupSerde {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(
                new SimpleModule()
                        .addSerializer(new RuleGroupSerializer())
                        .addSerializer(new RuleSerializer())
                );

    public static <InputType> RuleGroup<InputType> deserialize(String json) {
        return MAPPER.convertValue(json, RuleGroup.class);
    }

    public static <InputType> String serialize(RuleGroup<InputType> ruleGroup) {
        try {
            return MAPPER.writeValueAsString(ruleGroup);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
