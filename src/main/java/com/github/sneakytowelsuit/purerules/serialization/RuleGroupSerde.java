package com.github.sneakytowelsuit.purerules.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.sneakytowelsuit.purerules.conditions.*;
import com.github.sneakytowelsuit.purerules.exceptions.RuleGroupDeserializationException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class RuleGroupSerde<InputType> {
  private final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(
              new SimpleModule()
                  .addSerializer(new RuleGroupSerializer())
                  .addSerializer(new RuleSerializer()));
  @Getter private final Map<String, Field<InputType, ?>> fieldCache = new HashMap<>();
  @Getter private final Map<String, Operator<?>> operatorCache = new HashMap<>();

  public RuleGroup<InputType> deserialize(String json) {
    try {
      JsonNode jsonNode = MAPPER.readTree(json);
      if (jsonNode == null || !jsonNode.isObject()) {
        throw new RuleGroupDeserializationException(
            "Invalid JSON input for RuleGroup deserialization");
      }
      return deserializeJsonNodeToRuleGroup(jsonNode);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private RuleGroup<InputType> deserializeJsonNodeToRuleGroup(JsonNode jsonNode) {
    try {
      RuleGroup.RuleGroupBuilder<InputType> builder = RuleGroup.builder();
      deserializeRuleGroupId(jsonNode, builder);
      deserializePriority(jsonNode, builder);
      return builder
          .isInverted(deserializeInverted(jsonNode))
          .bias(deserializeBias(jsonNode))
          .combinator(deserializeCombinator(jsonNode))
          .conditions(deserializeConditions(jsonNode))
          .build();
    } catch (Exception e) {
      throw new RuleGroupDeserializationException("Error encountered deserializing RuleGroup", e);
    }
  }

  private void deserializeRuleGroupId(
      JsonNode jsonNode, RuleGroup.RuleGroupBuilder<InputType> builder) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.ID.getKey());
    if (node != null && node.isTextual()) {
      String id = node.asText();
      builder.id(id);
    }
  }

  private void deserializePriority(
      JsonNode jsonNode, RuleGroup.RuleGroupBuilder<InputType> builder) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.PRIORITY.getKey());
    if (node != null && node.isInt()) {
      Integer weight = node.asInt();
      builder.weight(weight);
    }
  }

  private boolean deserializeInverted(JsonNode jsonNode) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.INVERTED.getKey());
    if (node == null || !node.isBoolean()) {
      throw new RuleGroupDeserializationException(
          "Invalid or missing 'inverted' field in RuleGroup JSON");
    }
    return node.asBoolean();
  }

  private Bias deserializeBias(JsonNode jsonNode) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.BIAS.getKey());
    if (node == null || !node.isTextual()) {
      throw new RuleGroupDeserializationException(
          "Invalid or missing 'bias' field in RuleGroup JSON");
    }
    try {
      return Bias.valueOf(node.asText());
    } catch (IllegalArgumentException e) {
      throw new RuleGroupDeserializationException(
          "Exception encountered deserializing RuleGroup Bias", e);
    }
  }

  private Combinator deserializeCombinator(JsonNode jsonNode) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.COMBINATOR.getKey());
    if (node == null || !node.isTextual()) {
      throw new RuleGroupDeserializationException(
          "Invalid or missing 'combinator' field in RuleGroup JSON");
    }
    try {
      return Combinator.valueOf(node.asText());
    } catch (IllegalArgumentException e) {
      throw new RuleGroupDeserializationException(
          "Exception encountered deserializing RuleGroup Combinator", e);
    }
  }

  private List<Condition<InputType>> deserializeConditions(JsonNode jsonNode) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.CONDITIONS.getKey());
    if (node == null || !node.isArray()) {
      throw new RuleGroupDeserializationException("Missing or invalid 'conditions' array");
    }
    List<Condition<InputType>> conditions = new ArrayList<>();
    for (JsonNode conditionNode : node) {
      if (isRuleGroup(conditionNode)) {
        conditions.add(deserializeJsonNodeToRuleGroup(conditionNode));
      } else if (isRule(conditionNode)) {
        conditions.add(deserializeRule(conditionNode));
      } else {
        throw new RuleGroupDeserializationException("Unexpected node in 'conditions' array");
      }
    }
    return conditions;
  }

  private Rule<InputType, ?> deserializeRule(JsonNode jsonNode) {
    try {
      Rule.RuleBuilder<InputType, ?> builder =
          (Rule.RuleBuilder<InputType, ?>)
              Rule.builder()
                  .field((Field<Object, Object>) deserializeJsonNodeToField(jsonNode))
                  .operator((Operator<Object>) deserializeJsonNodeToOperator(jsonNode))
                  .value(deserializeJsonNodeToValue(jsonNode));
      deserializeRuleId(jsonNode, builder);
      deserializeRulePriority(jsonNode, builder);
      return builder.build();
    } catch (Exception e) {
      throw new RuleGroupDeserializationException("Error encountered deserializing Rule", e);
    }
  }

  private void deserializeRuleId(JsonNode jsonNode, Rule.RuleBuilder<InputType, ?> builder) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.ID.getKey());
    if (node != null && node.isTextual()) {
      String id = node.asText();
      builder.id(id);
    }
  }

  private void deserializeRulePriority(JsonNode jsonNode, Rule.RuleBuilder<InputType, ?> builder) {
    JsonNode node = jsonNode.get(RuleGroupJsonKeys.PRIORITY.getKey());
    if (node != null && node.isInt()) {
      Integer priority = node.asInt();
      builder.priority(priority);
    }
  }

  private Field<InputType, ?> deserializeJsonNodeToField(JsonNode jsonNode) {
    JsonNode fieldNode = jsonNode.get(RuleGroupJsonKeys.FIELD.getKey());
    if (fieldNode == null || !fieldNode.isTextual()) {
      throw new RuleGroupDeserializationException("Invalid or missing 'field' in Rule JSON");
    }
    String fieldClassName = fieldNode.asText();
    return this.getFieldCache()
        .computeIfAbsent(
            fieldClassName,
            className -> {
              try {
                Class<?> clazz = Class.forName(className);
                if (!Field.class.isAssignableFrom(clazz)) {
                  throw new RuleGroupDeserializationException(
                      "Class " + className + " is not a valid Field type");
                }
                return (Field<InputType, ?>) clazz.getDeclaredConstructor().newInstance();
              } catch (Exception e) {
                throw new RuleGroupDeserializationException(
                    "Exception encountered while deserializing RuleGroup Rule Field: "
                        + fieldClassName,
                    e);
              }
            });
  }

  private Operator<?> deserializeJsonNodeToOperator(JsonNode jsonNode) {
    JsonNode operatorNode = jsonNode.get(RuleGroupJsonKeys.OPERATOR.getKey());
    if (operatorNode == null || !operatorNode.isTextual()) {
      throw new RuleGroupDeserializationException("Invalid or missing 'operator' in Rule JSON");
    }
    String operatorClassName = operatorNode.asText();
    return this.getOperatorCache()
        .computeIfAbsent(
            operatorClassName,
            className -> {
              try {
                Class<?> clazz = Class.forName(className);
                if (!Operator.class.isAssignableFrom(clazz)) {
                  throw new RuleGroupDeserializationException(
                      "Class " + className + " is not a valid Operator type");
                }
                return (Operator<?>) clazz.getDeclaredConstructor().newInstance();
              } catch (InstantiationException
                  | IllegalAccessException
                  | InvocationTargetException
                  | NoSuchMethodException
                  | ClassNotFoundException
                  | ClassCastException e) {
                throw new RuleGroupDeserializationException(
                    "Exception encountered while deserializing RuleGroup Rule Operator: "
                        + operatorClassName,
                    e);
              }
            });
  }

  private <T> T deserializeJsonNodeToValue(JsonNode jsonNode) {
    JsonNode valueNode = jsonNode.get(RuleGroupJsonKeys.VALUE.getKey());
    if (valueNode == null || !valueNode.isObject()) {
      throw new RuleGroupDeserializationException("Invalid or missing 'value' in Rule JSON");
    }
    JsonNode valueClassNode = valueNode.get(RuleGroupJsonKeys.VALUE_CLASS.getKey());
    JsonNode valueValueNode = valueNode.get(RuleGroupJsonKeys.VALUE_VALUE.getKey());
    if (valueClassNode == null || !valueClassNode.isTextual() || valueValueNode == null) {
      throw new RuleGroupDeserializationException(
          "Invalid or missing 'value' class or value in Rule JSON");
    }
    String valueClassName = valueClassNode.asText();
    try {
      Class<?> clazz = Class.forName(valueClassName);
      return (T) MAPPER.convertValue(valueValueNode, clazz);
    } catch (ClassNotFoundException | ClassCastException e) {
      throw new RuleGroupDeserializationException(
          "Exception encountered while deserializing RuleGroup Rule Value: "
              + valueClassName
              + " with value "
              + valueValueNode.toPrettyString(),
          e);
    }
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
