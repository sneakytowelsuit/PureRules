package com.github.sneakytowelsuit.purerules.serialization;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.Bias;
import com.github.sneakytowelsuit.purerules.conditions.Combinator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import java.util.List;
import org.junit.jupiter.api.Test;

class RuleGroupSerializerTest {
  private ObjectMapper getMapper() {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(new RuleGroupSerializer());
    module.addSerializer(new RuleSerializer());
    mapper.registerModule(module);
    return mapper;
  }

  @Test
  void serialize_validRuleGroup_success() throws JsonProcessingException {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(1234)
            .build();
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .bias(Bias.INCLUSIVE)
            .isInverted(false)
            .conditions(List.of(rule))
            .build();

    String json = getMapper().writeValueAsString(group);
    assertTrue(json.contains(TestUtils.DummyField.class.getName()));
    assertTrue(json.contains(TestUtils.AlwaysTrueOperator.class.getName()));
    assertTrue(json.contains("1234"));
    assertTrue(json.contains("AND"));
    assertTrue(json.contains("INCLUSIVE"));
  }

  @Test
  void serialize_nullRuleGroup_throwsException() throws JsonProcessingException {
    assertEquals("null", getMapper().writeValueAsString(null));
  }

  @Test
  void serialize_nullCombinator_throwsException() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(null)
            .bias(Bias.INCLUSIVE)
            .isInverted(false)
            .conditions(List.of())
            .build();
    assertThrows(JsonMappingException.class, () -> getMapper().writeValueAsString(group));
  }

  @Test
  void serialize_nullBias_throwsException() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .bias(null)
            .isInverted(false)
            .conditions(List.of())
            .build();
    assertThrows(JsonMappingException.class, () -> getMapper().writeValueAsString(group));
  }

  @Test
  void serialize_nullConditions_throwsException() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .bias(Bias.INCLUSIVE)
            .isInverted(false)
            .conditions(null)
            .build();
    assertThrows(JsonMappingException.class, () -> getMapper().writeValueAsString(group));
  }

  @Test
  void serialize_nullConditionInList_throwsException() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .bias(Bias.INCLUSIVE)
            .isInverted(false)
            .conditions(null)
            .build();
    assertThrows(JsonMappingException.class, () -> getMapper().writeValueAsString(group));
  }
}
