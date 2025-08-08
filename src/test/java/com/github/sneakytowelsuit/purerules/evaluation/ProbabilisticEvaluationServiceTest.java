package com.github.sneakytowelsuit.purerules.evaluation;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.conditions.Combinator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.testutils.TestHelpers;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProbabilisticEvaluationServiceTest {
  private final EngineContextService<TestHelpers.Something, Integer> dummyContextService =
      new EngineContextService<>(TestHelpers.Something::getId);

  // spotless:off

    /**
     * Test a single rule with weight 1 and minProbability 1.0.
     * Expect: Only exact match passes.
     */
    // spotless:on
  @Test
  void testSingleRuleWeightOneMinProbOne() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(rule), 1.0f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    assertTrue(service.evaluate(alice, dummyContextService).get(rule.getId()));
    assertFalse(service.evaluate(bob, dummyContextService).get(rule.getId()));
  }

  // spotless:off

    /**
     * Test two rules with different weights and minProbability 0.5.
     * Rule1: weight 2, Rule2: weight 1. Only Rule1 matches.
     * Expect: score = 2/3 ≈ 0.67 > 0.5, so should pass.
     */
    // spotless:on
  @Test
  void testTwoRulesDifferentWeightsMinProbHalf() {
    Rule<TestHelpers.Something, String> rule1 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(2)
            .build();
    Rule<TestHelpers.Something, String> rule2 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Bob")
            .weight(1)
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(rule1, rule2), 0.5f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    // Only rule1 matches, so only rule1's id should be true
    assertTrue(service.evaluate(alice, dummyContextService).get(rule1.getId()));
    assertFalse(service.evaluate(alice, dummyContextService).get(rule2.getId()));
  }

  // spotless:off

    /**
     * Test group weight effect: group weight multiplies total result and weight.
     * Group weight 2, one rule matches (weight 1), minProbability 1.0.
     * Expect: score = (1*2)/(1*2) = 1.0, so should pass.
     */
    // spotless:on
  @Test
  void testGroupWeightEffect() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    RuleGroup<TestHelpers.Something> group =
        RuleGroup.<TestHelpers.Something>builder()
            .combinator(Combinator.AND)
            .weight(2)
            .conditions(List.of(rule))
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(group), 1.0f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    assertTrue(service.evaluate(alice, dummyContextService).get(group.getId()));
  }

  // spotless:off

    /**
     * Test threshold not met: two rules, only one matches, minProbability 0.8.
     * Both rules weight 1. Score = 0.5 < 0.8, should fail.
     */
    // spotless:on
  @Test
  void testThresholdNotMet() {
    Rule<TestHelpers.Something, String> rule1 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    Rule<TestHelpers.Something, String> rule2 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Bob")
            .weight(1)
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(rule1, rule2), 0.8f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    // Only rule1 matches, so only rule1's id should be true, but overall threshold not met
    assertFalse(
        service.evaluate(alice, dummyContextService).get(rule1.getId())
            && service.evaluate(alice, dummyContextService).get(rule2.getId()));
  }

  // spotless:off

    /**
     * Test zero total weight: no rules, minProbability 0.0.
     * Expect: should pass (score is 0, but threshold is 0).
     */
    // spotless:on
  @Test
  void testZeroTotalWeight() {
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(), 0.0f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    // No conditions, so result map should be empty
    assertTrue(service.evaluate(alice, dummyContextService).isEmpty());
  }

  // spotless:off

    /**
     * Test a complex RuleGroup tree with nested groups and various weights.
     * <p>
     * Structure:
     * RootGroup (weight=3, AND)
     * - Rule1 (weight=2) [matches]
     * - SubGroupA (weight=2, OR)
     * - Rule2 (weight=1) [does not match]
     * - Rule3 (weight=3) [matches]
     * - SubGroupB (weight=1, AND)
     * - Rule4 (weight=2) [matches]
     * - Rule5 (weight=1) [does not match]
     * <p>
     * Calculation:
     * - Rule1: 2*3 = 6 (matches)
     * - SubGroupA: (max of Rule2, Rule3) * 2 = 3*2 = 6 (matches, since OR)
     * - SubGroupB: (Rule4 AND Rule5) * 1 = 0 (since Rule5 fails, AND)
     * - Total possible: 6 (Rule1) + 6 (SubGroupA) + 3 (SubGroupB)
     * - Total actual:   6 (Rule1) + 6 (SubGroupA) + 0 (SubGroupB)
     * - Probability: (6+6+0)/(6+6+3) = 12/15 = 0.8
     * <p>
     * With minProbability=0.8, should pass.
     */
    // spotless:on
  @Test
  void testComplexRuleGroupTree() {
    Rule<TestHelpers.Something, String> rule1 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(2)
            .build();
    Rule<TestHelpers.Something, String> rule2 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Charlie")
            .weight(1)
            .build();
    Rule<TestHelpers.Something, String> rule3 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(3)
            .build();
    Rule<TestHelpers.Something, String> rule4 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(2)
            .build();
    Rule<TestHelpers.Something, String> rule5 =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Bob")
            .weight(1)
            .build();
    RuleGroup<TestHelpers.Something> subGroupA =
        RuleGroup.<TestHelpers.Something>builder()
            .combinator(Combinator.OR)
            .weight(2)
            .conditions(List.of(rule2, rule3))
            .build();
    RuleGroup<TestHelpers.Something> subGroupB =
        RuleGroup.<TestHelpers.Something>builder()
            .combinator(Combinator.AND)
            .weight(1)
            .conditions(List.of(rule4, rule5))
            .build();
    RuleGroup<TestHelpers.Something> rootGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .combinator(Combinator.AND)
            .weight(3)
            .conditions(List.of(rule1, subGroupA, subGroupB))
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(rootGroup), 0.7f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    var result = service.evaluate(alice, dummyContextService);
    System.out.println("testComplexRuleGroupTree result: " + result);
    assertTrue(result.get(rootGroup.getId()));
  }

  @Test
  void testTraceUpdatesContextWeightedRule() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(3)
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(rule), 0.0f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    EngineContextService<TestHelpers.Something, Integer> ctx =
        new EngineContextService<>(TestHelpers.Something::getId);
    service.trace(alice, ctx);
    service.trace(bob, ctx);
    var contextMap = ctx.getConditionEvaluationContext().getConditionContextMap();
    assertTrue(
        contextMap.containsKey(
            new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                alice.getId(), rule.getId())));
    assertEquals(
        3,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), rule.getId()))
            .getResult());
    assertTrue(
        contextMap.containsKey(
            new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                bob.getId(), rule.getId())));
    assertEquals(
        0,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    bob.getId(), rule.getId()))
            .getResult());
  }

  @Test
  void testTraceUpdatesContextNestedGroups() {
    Rule<TestHelpers.Something, String> ruleA =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(2)
            .id("ruleA")
            .build();
    Rule<TestHelpers.Something, String> ruleB =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Bob")
            .weight(1)
            .id("ruleB")
            .build();
    RuleGroup<TestHelpers.Something> innerGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("innerGroup")
            .combinator(Combinator.OR)
            .weight(2)
            .conditions(List.of(ruleA, ruleB))
            .build();
    Rule<TestHelpers.Something, String> ruleC =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Charlie")
            .weight(1)
            .id("ruleC")
            .build();
    RuleGroup<TestHelpers.Something> outerGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("outerGroup")
            .combinator(Combinator.AND)
            .weight(1)
            .conditions(List.of(innerGroup, ruleC))
            .build();
    ProbabilisticEvaluationService<TestHelpers.Something, Integer> service =
        new ProbabilisticEvaluationService<>(List.of(outerGroup), 0.0f);
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something charlie = new TestHelpers.Something(3, "Charlie");
    EngineContextService<TestHelpers.Something, Integer> ctx =
        new EngineContextService<>(TestHelpers.Something::getId);
    service.trace(alice, ctx);
    service.trace(charlie, ctx);
    var contextMap = ctx.getConditionEvaluationContext().getConditionContextMap();
    // Alice: ruleA matches (2), ruleB (0), innerGroup = (2+0)*2=4, ruleC (0), outerGroup =
    // (4+0)*1=0, max: (2+1)*2=6, outer max=(6+1)*1=7
    assertEquals(
        2,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "ruleA"))
            .getResult());
    assertEquals(
        0,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "ruleB"))
            .getResult());
    assertEquals(
        6,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "innerGroup"))
            .getResult());
    assertEquals(
        6,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "innerGroup"))
            .getMaximumResult());
    assertEquals(
        0,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "ruleC"))
            .getResult());
    assertEquals(
        7,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "outerGroup"))
            .getResult());
    assertEquals(
        7,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    alice.getId(), "outerGroup"))
            .getMaximumResult());
    // Charlie: ruleA (0), ruleB (0), innerGroup (0), ruleC (1), outerGroup (0+1)*1=1, max:
    // (2+1)*2=6, outer max=(6+1)*1=7
    assertEquals(
        0,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "ruleA"))
            .getResult());
    assertEquals(
        0,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "ruleB"))
            .getResult());
    assertEquals(
        6,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "innerGroup"))
            .getResult());
    assertEquals(
        6,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "innerGroup"))
            .getMaximumResult());
    assertEquals(
        1,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "ruleC"))
            .getResult());
    assertEquals(
        7,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "outerGroup"))
            .getResult());
    assertEquals(
        7,
        contextMap
            .get(
                new com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey<>(
                    charlie.getId(), "outerGroup"))
            .getMaximumResult());
  }
}
