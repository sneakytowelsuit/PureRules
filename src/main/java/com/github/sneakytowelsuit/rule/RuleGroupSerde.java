package com.github.sneakytowelsuit.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
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
            return deserializeJsonNodeToRuleGroup(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private RuleGroup<InputType> deserializeJsonNodeToRuleGroup(JsonNode jsonNode) throws JsonProcessingException {
        return RuleGroup.<InputType>builder()
                .isInverted(deserializeInverted(jsonNode))
                .bias(deserializeBias(jsonNode))
                .combinator(deserializeCombinator(jsonNode))
                .conditions(deserializeConditions(jsonNode))
                .build();
    }

    private boolean deserializeInverted(JsonNode jsonNode) {
        return jsonNode.get(RuleGroupJsonKeys.INVERTED.getKey()).asBoolean();
    }

    private Bias deserializeBias(JsonNode jsonNode) {
        try {
            return Bias.valueOf(jsonNode.get(RuleGroupJsonKeys.BIAS.getKey()).asText());
        } catch (IllegalArgumentException e) {
            throw new RuleGroupDeserializationException("Exception encountered deserializing RuleGroup Bias", e);
        }
    }

    private Combinator deserializeCombinator(JsonNode jsonNode) {
        try {
            return Combinator.valueOf(jsonNode.get(RuleGroupJsonKeys.COMBINATOR.getKey()).asText());
        } catch (IllegalArgumentException e) {
            throw new RuleGroupDeserializationException("Exception encountered deserializing RuleGroup Combinator", e);
        }
    }

    private List<Evaluator<InputType>> deserializeConditions(JsonNode jsonNode) {
        List<Evaluator<InputType>> evaluators = new ArrayList<>();
        ArrayNode nodeConditions = (ArrayNode) jsonNode.get(RuleGroupJsonKeys.CONDITIONS.getKey());
        if (nodeConditions.isArray()) {
            for (JsonNode conditionNode : nodeConditions) {
                if(isRuleGroup(conditionNode)){
                    try {
                        evaluators.add(deserializeJsonNodeToRuleGroup(conditionNode));
                    } catch (JsonProcessingException e) {
                        throw new RuleGroupDeserializationException("Exception encountered deserializing RuleGroup conditions", e);
                    }
                } else if (isRule(conditionNode)) {
                   evaluators.add(deserializeRule(conditionNode));
                } else {
                    throw new RuleGroupDeserializationException("Unexpected node encountered in RuleGroup conditions");
                }
            }
        }
       return evaluators;
    }

    private Rule<InputType, ?> deserializeRule(JsonNode jsonNode) {
        return (Rule<InputType, ?>) Rule.builder()
                .field((Field<Object, Object>) deserializeJsonNodeToField(jsonNode))
                .operator((Operator<Object, Object>) deserializeJsonNodeToOperator(jsonNode))
                .value(deserializeJsonNodeToValue(jsonNode))
                .build();
    }

    private Field<InputType, ?> deserializeJsonNodeToField(JsonNode jsonNode) {
       String fieldClassName = jsonNode.get(RuleGroupJsonKeys.FIELD.getKey()).asText();
       return this.getFieldCache().computeIfAbsent(fieldClassName,
               className -> {
                   try {
                       return (Field<InputType, ?>) Class.forName(className).getDeclaredConstructor().newInstance();
                   } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                            NoSuchMethodException | ClassNotFoundException | ClassCastException e) {
                       throw new RuleGroupDeserializationException("Exception encountered while deserializing RuleGroup Rule Field: " + fieldClassName, e);
                   }
               }
               );
    }

    private Operator<InputType, ?> deserializeJsonNodeToOperator(JsonNode jsonNode) {
        String operatorClassName = jsonNode.get(RuleGroupJsonKeys.OPERATOR.getKey()).asText();
        return this.getOperatorCache().computeIfAbsent(operatorClassName,
                className -> {
                    try {
                        return (Operator<InputType, ?>) Class.forName(className).getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                    NoSuchMethodException | ClassNotFoundException | ClassCastException e) {
                        throw new RuleGroupDeserializationException("Exception encountered while deserializing RuleGroup Rule Operator: " + operatorClassName, e);
                    }
                }
        );
    }

    private <T> T deserializeJsonNodeToValue(JsonNode jsonNode) {
        return ((T) jsonNode.get(RuleGroupJsonKeys.VALUE.getKey()));
    }

    private boolean isRule(JsonNode jsonNode) {
        return jsonNode.has(RuleGroupJsonKeys.FIELD.getKey())
                && jsonNode.has(RuleGroupJsonKeys.OPERATOR.getKey())
                && jsonNode.has(RuleGroupJsonKeys.VALUE.getKey());
    }

    private boolean isRuleGroup(JsonNode jsonNode) {
        return jsonNode.has(RuleGroupJsonKeys.COMBINATOR.getKey())
                && jsonNode.has(RuleGroupJsonKeys.INVERTED.getKey())
                && jsonNode.has(RuleGroupJsonKeys.BIAS.getKey())
                && jsonNode.has(RuleGroupJsonKeys.CONDITIONS.getKey());
    }

    public String serialize(RuleGroup<InputType> ruleGroup) {
        try {
            return MAPPER.writeValueAsString(ruleGroup);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
