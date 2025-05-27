package com.github.sneakytowelsuit.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleGroupSerde<InputType> {
    private final ObjectMapper MAPPER = new ObjectMapper().registerModule(
                new SimpleModule()
                        .addSerializer(new RuleGroupSerializer())
                        .addSerializer(new RuleSerializer())
                );
    @Getter
    private final Map<String, Field<InputType, ?>> fieldCache = new HashMap<>();
    @Getter
    private final Map<String, Operator<InputType, ?>> operatorCache = new HashMap<>();

    public RuleGroup<InputType> deserialize(String json) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            return RuleGroup.<InputType>builder()
                    .isInverted(deserializeInverted(jsonNode))
                    .bias(deserializeBias(jsonNode))
                    .combinator(deserializeCombinator(jsonNode))
                    .conditions(new ArrayList<>())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean deserializeInverted(JsonNode jsonNode) {
        return jsonNode.get("inverted").asBoolean();
    }

    private Bias deserializeBias(JsonNode jsonNode) {
        return Bias.valueOf(jsonNode.get("bias").asText());
    }

    private Combinator deserializeCombinator(JsonNode jsonNode) {
        return Combinator.valueOf(jsonNode.get("combinator").asText());
    }

    private List<Evaluator<InputType>> deserializeRules(JsonNode jsonNode) {
       return new ArrayList<>();
    }

    public String serialize(RuleGroup<InputType> ruleGroup) {
        try {
            return MAPPER.writeValueAsString(ruleGroup);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
