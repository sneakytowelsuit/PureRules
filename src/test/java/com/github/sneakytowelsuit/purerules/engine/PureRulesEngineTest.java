package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.Field;
import com.github.sneakytowelsuit.purerules.conditions.Operator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PureRulesEngineTest {

  private class Something {
    private final String name;
    private final int id;

    public Something(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public int getId() {
      return id;
    }
  }

  private class SomethingNameField implements Field<Something, String> {
    @Override
    public Function<Something, String> getFieldValueFunction() {
      return Something::getName;
    }
  }

  private class StringEqualsCaseInsensitiveOperator implements Operator<String> {

    @Override
    public boolean test(String input, String s) {
      return input.toLowerCase(Locale.ROOT).equals(s.toLowerCase(Locale.ROOT));
    }
  }

  private class StringEqualsCaseSensitiveOperator implements Operator<String> {

    @Override
    public boolean test(String input, String s) {
      return input.equals(s);
    }
  }

  @Test
  void testDeterministicEngine_caseSensitive() {
    // Test case-sensitive string comparison
    Rule<Something, String> rule = Rule.<Something, String>builder()
            .field(new SomethingNameField())
            .operator(new StringEqualsCaseSensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    PureRulesEngine<Something, Integer> engine = PureRulesEngine.getDeterministicEngine(Something::getId, List.of(rule));
    Something alice = new Something(1, "Alice");
    Something bob = new Something(2, "Bob");
    Map<String, Boolean> resultAlice = engine.evaluate(alice);
    Map<String, Boolean> resultBob = engine.evaluate(bob);
    assertTrue(resultAlice.values().iterator().next());
    assertFalse(resultBob.values().iterator().next());
  }

  @Test
  void testDeterministicEngine_caseInsensitive() {
    Rule<Something, String> rule = Rule.<Something, String>builder()
            .field(new SomethingNameField())
            .operator(new StringEqualsCaseInsensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    PureRulesEngine<Something, Integer> engine = PureRulesEngine.getDeterministicEngine(Something::getId, List.of(rule));
    Something alice = new Something(1, "Alice");
    Something aliceLower = new Something(2, "alice");
    Map<String, Boolean> resultAlice = engine.evaluate(alice);
    Map<String, Boolean> resultAliceLower = engine.evaluate(aliceLower);
    assertTrue(resultAlice.values().iterator().next());
    assertTrue(resultAliceLower.values().iterator().next());
  }

  @Test
  void testEvaluateAll() {
    Rule<Something, String> rule = Rule.<Something, String>builder()
            .field(new SomethingNameField())
            .operator(new StringEqualsCaseInsensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    PureRulesEngine<Something, Integer> engine = PureRulesEngine.getDeterministicEngine(Something::getId, List.of(rule));
    Something alice = new Something(1, "Alice");
    Something bob = new Something(2, "Bob");
    Map<Integer, Map<String, Boolean>> results = engine.evaluateAll(Arrays.asList(alice, bob));
    assertTrue(results.get(1).values().iterator().next());
    assertFalse(results.get(2).values().iterator().next());
  }

  @Test
  void testProbabilisticEngine() {
    Rule<Something, String> failingRule = Rule.<Something, String>builder()
            .field(new SomethingNameField())
            .operator(new StringEqualsCaseSensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    Rule<Something, String> passingRule = Rule.<Something, String>builder()
            .field(new SomethingNameField())
            .operator(new StringEqualsCaseInsensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    RuleGroup<Something> ruleGroup = RuleGroup.<Something>builder()
            .conditions(List.of(failingRule, passingRule))
            .build();
    PureRulesEngine<Something, Integer> engine = PureRulesEngine.getProbabilisticEngine(Something::getId, 0.5f, List.of(ruleGroup));
    Something alice = new Something(1, "Alice");
    Map<String, Boolean> result = engine.evaluate(alice);
    assertTrue(result.values().iterator().next());
  }

}
