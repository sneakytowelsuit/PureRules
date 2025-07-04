package com.github.sneakytowelsuit.purerules.engine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sneakytowelsuit.purerules.conditions.Operator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.testutils.TestHelpers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PureRulesEngineTest {

  private static class StringEqualsCaseSensitiveOperator implements Operator<String> {
    @Override
    public boolean test(String input, String s) {
      return input.equals(s);
    }
  }

  @Test
  void testDeterministicEngine_caseSensitive() {
    // Test case-sensitive string comparison
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new StringEqualsCaseSensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    PureRulesEngine<TestHelpers.Something, Integer> engine =
        PureRulesEngine.getDeterministicEngine(TestHelpers.Something::getId, List.of(rule));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    Map<String, Boolean> resultAlice = engine.evaluate(alice);
    Map<String, Boolean> resultBob = engine.evaluate(bob);
    assertTrue(resultAlice.values().iterator().next());
    assertFalse(resultBob.values().iterator().next());
  }

  @Test
  void testDeterministicEngine_caseInsensitive() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    PureRulesEngine<TestHelpers.Something, Integer> engine =
        PureRulesEngine.getDeterministicEngine(TestHelpers.Something::getId, List.of(rule));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something aliceLower = new TestHelpers.Something(2, "alice");
    Map<String, Boolean> resultAlice = engine.evaluate(alice);
    Map<String, Boolean> resultAliceLower = engine.evaluate(aliceLower);
    assertTrue(resultAlice.values().iterator().next());
    assertTrue(resultAliceLower.values().iterator().next());
  }

  @Test
  void testEvaluateAll() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    PureRulesEngine<TestHelpers.Something, Integer> engine =
        PureRulesEngine.getDeterministicEngine(TestHelpers.Something::getId, List.of(rule));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    Map<Integer, Map<String, Boolean>> results = engine.evaluateAll(Arrays.asList(alice, bob));
    assertTrue(results.get(1).values().iterator().next());
    assertFalse(results.get(2).values().iterator().next());
  }

  @Test
  void testProbabilisticEngine() {
    Rule<TestHelpers.Something, String> failingRule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new StringEqualsCaseSensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    Rule<TestHelpers.Something, String> passingRule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("alice")
            .weight(1)
            .build();
    RuleGroup<TestHelpers.Something> ruleGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .conditions(List.of(failingRule, passingRule))
            .build();
    PureRulesEngine<TestHelpers.Something, Integer> engine =
        PureRulesEngine.getProbabilisticEngine(
            TestHelpers.Something::getId, 0.5f, List.of(ruleGroup));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    Map<String, Boolean> result = engine.evaluate(alice);
    assertTrue(result.values().iterator().next());
  }
}
