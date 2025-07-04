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

  @Test
  void testDeserializeListWithValidArray() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json =
        "["
            + "{\"id\": \"id1\", \"inverted\": false, \"bias\": \"INCLUSIVE\", \"combinator\": \"AND\", \"conditions\": []},"
            + "{\"id\": \"id2\", \"inverted\": true, \"bias\": \"EXCLUSIVE\", \"combinator\": \"OR\", \"conditions\": []}"
            + "]";
    var groups = serde.deserializeList(json);
    assertEquals(2, groups.size());
    assertEquals("id1", groups.get(0).getId());
    assertEquals("id2", groups.get(1).getId());
    assertFalse(groups.get(0).isInverted());
    assertTrue(groups.get(1).isInverted());
    assertEquals(Bias.INCLUSIVE, groups.get(0).getBias());
    assertEquals(Bias.EXCLUSIVE, groups.get(1).getBias());
    assertEquals(Combinator.AND, groups.get(0).getCombinator());
    assertEquals(Combinator.OR, groups.get(1).getCombinator());
    assertTrue(groups.get(0).getConditions().isEmpty());
    assertTrue(groups.get(1).getConditions().isEmpty());
  }

  @Test
  void testDeserializeListWithInvalidArrayThrows() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json = "[1, 2, 3]";
    assertThrows(RuleGroupDeserializationException.class, () -> serde.deserializeList(json));
  }

  @Test
  void testDeserializeWithArrayInputThrows() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String json = "[{}]";
    assertThrows(RuleGroupDeserializationException.class, () -> serde.deserialize(json));
  }

  @Test
  void testDeserializeSingleRule() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String fieldClass = TestUtils.DummyField.class.getName();
    String operatorClass = TestUtils.AlwaysTrueOperator.class.getName();
    String json =
        "{"
            + "\"field\": \""
            + fieldClass
            + "\","
            + "\"operator\": \""
            + operatorClass
            + "\","
            + "\"value\": {\"class\": \"java.lang.String\", \"value\": \"foo\"}"
            + "}";
    Rule<String, ?> rule = serde.deserializeRule(json);
    assertNotNull(rule);
    assertEquals("foo", rule.getValue());
    assertTrue(rule.getField() instanceof TestUtils.DummyField);
    assertTrue(rule.getOperator() instanceof TestUtils.AlwaysTrueOperator);
  }

  @Test
  void testDeserializeRuleListWithArray() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String fieldClass = TestUtils.DummyField.class.getName();
    String operatorClass = TestUtils.AlwaysTrueOperator.class.getName();
    String json =
        "["
            + "{"
            + "\"field\": \""
            + fieldClass
            + "\","
            + "\"operator\": \""
            + operatorClass
            + "\","
            + "\"value\": {\"class\": \"java.lang.String\", \"value\": \"foo\"}"
            + "},"
            + "{"
            + "\"field\": \""
            + fieldClass
            + "\","
            + "\"operator\": \""
            + operatorClass
            + "\","
            + "\"value\": {\"class\": \"java.lang.String\", \"value\": \"bar\"}"
            + "}"
            + "]";
    var rules = serde.deserializeRuleList(json);
    assertEquals(2, rules.size());
    assertEquals("foo", rules.get(0).getValue());
    assertEquals("bar", rules.get(1).getValue());
  }

  @Test
  void testDeserializeRuleListWithSingleObject() {
    RuleGroupSerde<String> serde = new RuleGroupSerde<>();
    String fieldClass = TestUtils.DummyField.class.getName();
    String operatorClass = TestUtils.AlwaysTrueOperator.class.getName();
    String json =
        "{"
            + "\"field\": \""
            + fieldClass
            + "\","
            + "\"operator\": \""
            + operatorClass
            + "\","
            + "\"value\": {\"class\": \"java.lang.String\", \"value\": \"baz\"}"
            + "}";
    var rules = serde.deserializeRuleList(json);
    assertEquals(1, rules.size());
    assertEquals("baz", rules.get(0).getValue());
  }
}
