package com.github.sneakytowelsuit.purerules.serialization;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.*;
import com.github.sneakytowelsuit.purerules.exceptions.RuleGroupDeserializationException;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class RuleGroupSerdeTest {

  @Test
  void testDeserializeValidJson() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json =
        "{\"id\": \"testId1234\", \"inverted\": false, \"bias\": \"INCLUSIVE\", \"combinator\": \"AND\", \"conditions\": []}";

    RuleGroup<String> ruleGroup = serde.deserialize(json);

    assertNotNull(ruleGroup);
    assertFalse(ruleGroup.isInverted());
    assertEquals(Bias.INCLUSIVE, ruleGroup.getBias());
    assertEquals(Combinator.AND, ruleGroup.getCombinator());
    assertTrue(ruleGroup.getConditions().isEmpty());
    assertEquals("testId1234", ruleGroup.getId());
  }

  @Test
  void testDeserializeInvalidJsonThrows() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json = "not a json";
    assertThrows(RuntimeException.class, () -> serde.deserialize(json));
  }

  @Test
  void testDeserializeMissingFieldsThrows() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json = "{\"bias\": \"NEUTRAL\", \"combinator\": \"AND\", \"conditions\": []}";
    assertThrows(RuleGroupDeserializationException.class, () -> serde.deserialize(json));
  }

  @Test
  void testDeserializeInvalidBiasThrows() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json =
        "{\"inverted\": false, \"bias\": \"INVALID\", \"combinator\": \"AND\", \"conditions\": []}";
    assertThrows(RuleGroupDeserializationException.class, () -> serde.deserialize(json));
  }

  @Test
  void testDeserializeInvalidCombinatorThrows() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json =
        "{\"inverted\": false, \"bias\": \"NEUTRAL\", \"combinator\": \"INVALID\", \"conditions\": []}";
    assertThrows(RuleGroupDeserializationException.class, () -> serde.deserialize(json));
  }

  @Test
  void testDeserializeWithRuleCondition() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    // Use a dummy field and operator class for testing
    String fieldClass = TestUtils.DummyField.class.getName();
    String operatorClass = TestUtils.AlwaysTrueOperator.class.getName();
    String json =
        "{"
            + "\"inverted\": false, "
            + "\"bias\": \"INCLUSIVE\", "
            + "\"combinator\": \"AND\", "
            + "\"conditions\": ["
            + "  {"
            + "    \"field\": \""
            + fieldClass
            + "\","
            + "    \"operator\": \""
            + operatorClass
            + "\","
            + "    \"value\": {"
            + "      \"class\": \"java.lang.String\","
            + "      \"value\": \"test-value\""
            + "    }"
            + "  }"
            + "]"
            + "}";

    RuleGroup<String> ruleGroup = serde.deserialize(json);
    assertNotNull(ruleGroup);
    assertEquals(1, ruleGroup.getConditions().size());
    Condition<String> cond = ruleGroup.getConditions().get(0);
    assertTrue(cond instanceof Rule);
    Rule<String, ?> rule = (Rule<String, ?>) cond;
    assertTrue(rule.getField() instanceof TestUtils.DummyField);
    assertTrue(rule.getOperator() instanceof TestUtils.AlwaysTrueOperator);
    assertEquals("test-value", rule.getValue());
  }

  @Test
  void testDeserializeWithNestedRuleGroup() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json =
        "{"
            + "\"inverted\": false, "
            + "\"bias\": \"INCLUSIVE\", "
            + "\"combinator\": \"AND\", "
            + "\"conditions\": ["
            + "  {"
            + "    \"inverted\": true, "
            + "    \"bias\": \"INCLUSIVE\", "
            + "    \"combinator\": \"OR\", "
            + "    \"conditions\": []"
            + "  }"
            + "]"
            + "}";

    RuleGroup<String> ruleGroup = serde.deserialize(json);
    assertNotNull(ruleGroup);
    assertEquals(1, ruleGroup.getConditions().size());
    Condition<String> cond = ruleGroup.getConditions().get(0);
    assertInstanceOf(RuleGroup.class, cond);
    RuleGroup<String> nested = (RuleGroup<String>) cond;
    assertTrue(nested.isInverted());
    assertEquals(Bias.INCLUSIVE, nested.getBias());
    assertEquals(Combinator.OR, nested.getCombinator());
    assertTrue(nested.getConditions().isEmpty());
  }

  @Test
  void testSerializeAndDeserializeRoundTrip() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .isInverted(true)
            .bias(Bias.INCLUSIVE)
            .combinator(Combinator.OR)
            .conditions(Collections.emptyList())
            .build();

    String json = serde.serialize(group);
    RuleGroup<String> deserialized = serde.deserialize(json);
    assertEquals(group.isInverted(), deserialized.isInverted());
    assertEquals(group.getBias(), deserialized.getBias());
    assertEquals(group.getCombinator(), deserialized.getCombinator());
    assertEquals(group.getConditions().size(), deserialized.getConditions().size());
  }
}
