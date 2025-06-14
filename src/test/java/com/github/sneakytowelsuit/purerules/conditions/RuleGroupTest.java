package com.github.sneakytowelsuit.purerules.conditions;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.TestUtils;
import java.util.List;

import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import org.junit.jupiter.api.Test;

class RuleGroupTest {

  @Test
  void testAndCombinatorAllTrue() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysTrueRule()))
            .build();
    assertTrue(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testAndCombinatorOneFalse() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysFalseRule()))
            .build();
    assertFalse(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testOrCombinatorAllFalse() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.OR)
            .conditions(List.of(TestUtils.alwaysFalseRule(), TestUtils.alwaysFalseRule()))
            .build();
    assertFalse(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testOrCombinatorOneTrue() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.OR)
            .conditions(List.of(TestUtils.alwaysFalseRule(), TestUtils.alwaysTrueRule()))
            .build();
    assertTrue(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testInvertedResult() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .isInverted(true)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysTrueRule()))
            .build();
    assertFalse(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testEmptyConditionsExclusiveBias() {
    RuleGroup<String> group =
        RuleGroup.<String>builder().conditions(List.of()).bias(Bias.EXCLUSIVE).build();
    assertFalse(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testEmptyConditionsInclusiveBias() {
    RuleGroup<String> group =
        RuleGroup.<String>builder().conditions(List.of()).bias(Bias.INCLUSIVE).build();
    assertTrue(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testEmptyConditionsInvertedBias() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .conditions(List.of())
            .bias(Bias.INCLUSIVE)
            .isInverted(true)
            .build();
    assertFalse(group.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testNestedGroupsAnd() {
    RuleGroup<String> inner =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysTrueRule()))
            .build();
    RuleGroup<String> outer =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(inner, TestUtils.alwaysTrueRule()))
            .build();
    assertTrue(outer.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testNestedGroupsOr() {
    RuleGroup<String> inner =
        RuleGroup.<String>builder()
            .combinator(Combinator.OR)
            .conditions(List.of(TestUtils.alwaysFalseRule(), TestUtils.alwaysTrueRule()))
            .build();
    RuleGroup<String> outer =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(inner, TestUtils.alwaysTrueRule()))
            .build();
    assertTrue(outer.evaluate("input", EngineMode.DETERMINISTIC));
  }

  @Test
  void testDeeplyNestedGroups() {
    RuleGroup<String> level3 =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(TestUtils.alwaysTrueRule()))
            .build();
    RuleGroup<String> level2 =
        RuleGroup.<String>builder().combinator(Combinator.AND).conditions(List.of(level3)).build();
    RuleGroup<String> level1 =
        RuleGroup.<String>builder().combinator(Combinator.AND).conditions(List.of(level2)).build();
    assertTrue(level1.evaluate("input", EngineMode.DETERMINISTIC));
  }
}
