package com.github.sneakytowelsuit.purerules.serialization;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RuleSerializerTest {
  @Test
  void testSerializeRule() throws JsonProcessingException {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(1234)
            .build();

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(new RuleSerializer());
    mapper.registerModule(module);

    String json = mapper.writeValueAsString(rule);

    assertTrue(json.contains("\"field\":\"" + TestUtils.DummyField.class.getName() + "\""));
    assertTrue(
        json.contains("\"operator\":\"" + TestUtils.AlwaysTrueOperator.class.getName() + "\""));
    assertTrue(json.contains("\"datatype\":\"" + Integer.class.getName() + "\""));
    assertTrue(json.contains("1234"));
  }

  @ParameterizedTest
  @MethodSource("ruleSerializationTestCases")
  void testSerializeRuleWithVariousNullValues(Rule<String, Integer> rule) {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(new RuleSerializer());
    mapper.registerModule(module);

    assertThrows(JsonMappingException.class, () -> mapper.writeValueAsString(rule));
  }

  static Stream<Arguments> ruleSerializationTestCases() {
    return Stream.of(
        Arguments.of(
            Rule.<String, Integer>builder()
                .field(null)
                .operator(new TestUtils.AlwaysTrueOperator())
                .value(1234)
                .build()),
        Arguments.of(
            Rule.<String, Integer>builder()
                .field(new TestUtils.DummyField())
                .operator(null)
                .value(1234)
                .build()),
        Arguments.of(
            Rule.<String, Integer>builder()
                .field(new TestUtils.DummyField())
                .operator(new TestUtils.AlwaysTrueOperator())
                .value(null)
                .build()));
  }
}
