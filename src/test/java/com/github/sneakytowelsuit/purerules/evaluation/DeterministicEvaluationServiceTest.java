package com.github.sneakytowelsuit.purerules.evaluation;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.conditions.Combinator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.testutils.TestHelpers;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DeterministicEvaluationServiceTest {

  private final EngineContextService<TestHelpers.Something, Integer> dummyContextService =
      new EngineContextService<>(TestHelpers.Something::getId);

  @Test
  void testEvaluateWithSingleRule() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(rule));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    Map<String, Boolean> resultAlice = service.evaluate(alice, dummyContextService);
    Map<String, Boolean> resultBob = service.evaluate(bob, dummyContextService);
    assertTrue(resultAlice.get(rule.getId()));
    assertFalse(resultBob.get(rule.getId()));
  }

  @Test
  void testEvaluateWithRuleGroupAllTrue() {
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
            .value("Alice")
            .weight(1)
            .build();
    RuleGroup<TestHelpers.Something> group =
        RuleGroup.<TestHelpers.Something>builder()
            .id("group1")
            .combinator(Combinator.AND)
            .conditions(List.of(rule1, rule2))
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(group));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    Map<String, Boolean> result = service.evaluate(alice, dummyContextService);
    assertTrue(result.get("group1"));
  }

  @Test
  void testEvaluateWithRuleGroupAllFalse() {
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
    RuleGroup<TestHelpers.Something> group =
        RuleGroup.<TestHelpers.Something>builder()
            .id("group2")
            .combinator(Combinator.AND)
            .conditions(List.of(rule1, rule2))
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(group));
    TestHelpers.Something charlie = new TestHelpers.Something(3, "Charlie");
    Map<String, Boolean> result = service.evaluate(charlie, dummyContextService);
    assertFalse(result.get("group2"));
  }

  @Test
  void testEvaluateWithMixedRuleAndGroup() {
    Rule<TestHelpers.Something, String> rule =
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
    RuleGroup<TestHelpers.Something> group =
        RuleGroup.<TestHelpers.Something>builder()
            .id("group3")
            .combinator(Combinator.OR)
            .conditions(List.of(rule, rule2))
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(rule, group));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    Map<String, Boolean> resultAlice = service.evaluate(alice, dummyContextService);
    Map<String, Boolean> resultBob = service.evaluate(bob, dummyContextService);
    assertTrue(resultAlice.get(rule.getId()));
    assertTrue(resultAlice.get("group3"));
    assertFalse(resultBob.get(rule.getId()));
    assertTrue(resultBob.get("group3"));
  }

  @Test
  void testEvaluateWithNestedGroups() {
    Rule<TestHelpers.Something, String> ruleA =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    Rule<TestHelpers.Something, String> ruleB =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Bob")
            .weight(1)
            .build();
    RuleGroup<TestHelpers.Something> innerGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("inner")
            .combinator(Combinator.OR)
            .conditions(List.of(ruleA, ruleB))
            .build();
    RuleGroup<TestHelpers.Something> outerGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("outer")
            .combinator(Combinator.AND)
            .conditions(List.of(innerGroup))
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(outerGroup));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    TestHelpers.Something charlie = new TestHelpers.Something(3, "Charlie");
    assertTrue(service.evaluate(alice, dummyContextService).get("outer"));
    assertTrue(service.evaluate(bob, dummyContextService).get("outer"));
    assertFalse(service.evaluate(charlie, dummyContextService).get("outer"));
  }

  @Test
  void testEvaluateWithEmptyRuleGroup() {
    RuleGroup<TestHelpers.Something> emptyGroup =
        RuleGroup.<TestHelpers.Something>builder().id("empty").combinator(Combinator.AND).build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(emptyGroup));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    // By default, empty AND group should be false (EXCLUSIVE bias)
    assertFalse(service.evaluate(alice, dummyContextService).get("empty"));
  }

  @Test
  void testEvaluateWithEmptyRuleGroupInclusiveBias() {
    RuleGroup<TestHelpers.Something> emptyGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("empty_inclusive")
            .combinator(Combinator.AND)
            .bias(com.github.sneakytowelsuit.purerules.conditions.Bias.INCLUSIVE)
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(emptyGroup));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    // INCLUSIVE bias: empty group should evaluate to true
    assertTrue(service.evaluate(alice, dummyContextService).get("empty_inclusive"));
  }

  @Test
  void testEvaluateWithEmptyRuleGroupExclusiveBias() {
    RuleGroup<TestHelpers.Something> emptyGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("empty_exclusive")
            .combinator(Combinator.AND)
            .bias(com.github.sneakytowelsuit.purerules.conditions.Bias.EXCLUSIVE)
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(emptyGroup));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    // EXCLUSIVE bias: empty group should evaluate to false
    assertFalse(service.evaluate(alice, dummyContextService).get("empty_exclusive"));
  }

  @Test
  void testTraceUpdatesContext() {
    Rule<TestHelpers.Something, String> rule =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .weight(1)
            .build();
    DeterministicEvaluationService<TestHelpers.Something, Integer> service =
        new DeterministicEvaluationService<>(List.of(rule));
    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    service.trace(alice, dummyContextService);
    service.trace(bob, dummyContextService);
    // Assert context for Alice exists and is correct
    assertTrue(
        dummyContextService
            .getConditionEvaluationContext()
            .getConditionContextMap()
            .containsKey(new ConditionContextKey<>(alice.getId(), rule.getId())));
    assertEquals(
        1,
        dummyContextService
            .getConditionEvaluationContext()
            .getConditionContextMap()
            .get(new ConditionContextKey<>(alice.getId(), rule.getId()))
            .getResult());
    // Assert context for Bob exists and is correct
    assertTrue(
        dummyContextService
            .getConditionEvaluationContext()
            .getConditionContextMap()
            .containsKey(new ConditionContextKey<>(bob.getId(), rule.getId())));
    assertEquals(
        0,
        dummyContextService
            .getConditionEvaluationContext()
            .getConditionContextMap()
            .get(new ConditionContextKey<>(bob.getId(), rule.getId()))
            .getResult());
  }

  @Test
  void testTraceUpdatesContextComplexRuleGroup() {
    // Create rules
    Rule<TestHelpers.Something, String> ruleA =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Alice")
            .id("aliceRule")
            .build();
    Rule<TestHelpers.Something, String> ruleB =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Bob")
            .id("bobRule")
            .build();
    Rule<TestHelpers.Something, String> ruleC =
        Rule.<TestHelpers.Something, String>builder()
            .field(new TestHelpers.SomethingNameField())
            .operator(new TestHelpers.StringEqualsCaseInsensitiveOperator())
            .value("Charlie")
            .id("charlieRule")
            .build();

    // Nested group: (ruleA OR ruleB)
    RuleGroup<TestHelpers.Something> innerGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("innerGroup")
            .combinator(Combinator.OR)
            .conditions(List.of(ruleA, ruleB))
            .build();
    // Outer group: (innerGroup AND ruleC)
    RuleGroup<TestHelpers.Something> outerGroup =
        RuleGroup.<TestHelpers.Something>builder()
            .id("outerGroup")
            .combinator(Combinator.AND)
            .conditions(List.of(innerGroup, ruleC))
            .build();

    DeterministicEvaluationService<TestHelpers.Something, String> service =
        new DeterministicEvaluationService<>(List.of(outerGroup));

    TestHelpers.Something alice = new TestHelpers.Something(1, "Alice");
    TestHelpers.Something bob = new TestHelpers.Something(2, "Bob");
    TestHelpers.Something charlie = new TestHelpers.Something(3, "Charlie");
    TestHelpers.Something dave = new TestHelpers.Something(4, "Dave");

    var ctxService = new EngineContextService<>(TestHelpers.Something::getName);
    // Trace all
    service.trace(alice, ctxService);
    service.trace(bob, ctxService);
    service.trace(charlie, ctxService);
    service.trace(dave, ctxService);

    // Helper for context assertions
    var contextMap = ctxService.getConditionEvaluationContext().getConditionContextMap();
    // Alice: ruleA true, ruleB false, ruleC false, innerGroup true, outerGroup false
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(alice.getName(), ruleA.getId())));
    assertEquals(
        1, contextMap.get(new ConditionContextKey<>(alice.getName(), ruleA.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(alice.getName(), ruleB.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(alice.getName(), ruleB.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(alice.getName(), ruleC.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(alice.getName(), ruleC.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(alice.getName(), "innerGroup")));
    assertEquals(
        1, contextMap.get(new ConditionContextKey<>(alice.getName(), "innerGroup")).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(alice.getName(), "outerGroup")));
    assertEquals(
        1, contextMap.get(new ConditionContextKey<>(alice.getName(), "outerGroup")).getResult());

    // Bob: ruleA false, ruleB true, ruleC false, innerGroup true, outerGroup false
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(bob.getName(), ruleA.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(bob.getName(), ruleA.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(bob.getName(), ruleB.getId())));
    assertEquals(
        1, contextMap.get(new ConditionContextKey<>(bob.getName(), ruleB.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(bob.getName(), ruleC.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(bob.getName(), ruleC.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(bob.getName(), "innerGroup")));
    assertEquals(
        1, contextMap.get(new ConditionContextKey<>(bob.getName(), "innerGroup")).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(bob.getName(), "outerGroup")));
    assertEquals(
        1, contextMap.get(new ConditionContextKey<>(bob.getName(), "outerGroup")).getResult());

    assertTrue(
        contextMap.containsKey(new ConditionContextKey<>(charlie.getName(), ruleA.getId())),
        "Missing context for Charlie/ruleA: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleA.getId())));
    assertEquals(
        0,
        contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleA.getId())).getResult(),
        "Charlie/ruleA result: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleA.getId())));
    assertTrue(
        contextMap.containsKey(new ConditionContextKey<>(charlie.getName(), ruleB.getId())),
        "Missing context for Charlie/ruleB: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleB.getId())));
    assertEquals(
        0,
        contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleB.getId())).getResult(),
        "Charlie/ruleB result: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleB.getId())));
    assertTrue(
        contextMap.containsKey(new ConditionContextKey<>(charlie.getName(), ruleC.getId())),
        "Missing context for Charlie/ruleC: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleC.getId())));
    assertEquals(
        1,
        contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleC.getId())).getResult(),
        "Charlie/ruleC result: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), ruleC.getId())));
    assertTrue(
        contextMap.containsKey(new ConditionContextKey<>(charlie.getName(), "innerGroup")),
        "Missing context for Charlie/innerGroup: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), "innerGroup")));
    assertEquals(
        0,
        contextMap.get(new ConditionContextKey<>(charlie.getName(), "innerGroup")).getResult(),
        "Charlie/innerGroup result: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), "innerGroup")));
    assertTrue(
        contextMap.containsKey(new ConditionContextKey<>(charlie.getName(), "outerGroup")),
        "Missing context for Charlie/outerGroup: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), "outerGroup")));
    assertEquals(
        1,
        contextMap.get(new ConditionContextKey<>(charlie.getName(), "outerGroup")).getResult(),
        "Charlie/outerGroup result: "
            + contextMap.get(new ConditionContextKey<>(charlie.getName(), "outerGroup")));

    // Dave: ruleA false, ruleB false, ruleC false, innerGroup false, outerGroup false
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(dave.getName(), ruleA.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(dave.getName(), ruleA.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(dave.getName(), ruleB.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(dave.getName(), ruleB.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(dave.getName(), ruleC.getId())));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(dave.getName(), ruleC.getId())).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(dave.getName(), "innerGroup")));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(dave.getName(), "innerGroup")).getResult());
    assertTrue(contextMap.containsKey(new ConditionContextKey<>(dave.getName(), "outerGroup")));
    assertEquals(
        0, contextMap.get(new ConditionContextKey<>(dave.getName(), "outerGroup")).getResult());
  }
}
