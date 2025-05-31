package com.github.sneakytowelsuit.purerules.conditions;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.context.EngineContext;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RuleGroupTest {
  @BeforeAll
  static void setUp() {
    EngineContext.getInstance();
  }

  @AfterEach
  void tearDown() {
    EngineContext.getInstance().flushAll();
  }

  @Test
  void testAndCombinatorAllTrue() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysTrueRule()))
            .build();
    assertTrue(group.evaluate("input"));
  }

  @Test
  void testAndCombinatorOneFalse() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysFalseRule()))
            .build();
    assertFalse(group.evaluate("input"));
  }

  @Test
  void testOrCombinatorAllFalse() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.OR)
            .conditions(List.of(TestUtils.alwaysFalseRule(), TestUtils.alwaysFalseRule()))
            .build();
    assertFalse(group.evaluate("input"));
  }

  @Test
  void testOrCombinatorOneTrue() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.OR)
            .conditions(List.of(TestUtils.alwaysFalseRule(), TestUtils.alwaysTrueRule()))
            .build();
    assertTrue(group.evaluate("input"));
  }

  @Test
  void testInvertedResult() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .combinator(Combinator.AND)
            .isInverted(true)
            .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysTrueRule()))
            .build();
    assertFalse(group.evaluate("input"));
  }

  @Test
  void testEmptyConditionsExclusiveBias() {
    RuleGroup<String> group =
        RuleGroup.<String>builder().conditions(List.of()).bias(Bias.EXCLUSIVE).build();
    assertFalse(group.evaluate("input"));
  }

  @Test
  void testEmptyConditionsInclusiveBias() {
    RuleGroup<String> group =
        RuleGroup.<String>builder().conditions(List.of()).bias(Bias.INCLUSIVE).build();
    assertTrue(group.evaluate("input"));
  }

  @Test
  void testEmptyConditionsInvertedBias() {
    RuleGroup<String> group =
        RuleGroup.<String>builder()
            .conditions(List.of())
            .bias(Bias.INCLUSIVE)
            .isInverted(true)
            .build();
    assertFalse(group.evaluate("input"));
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
    assertTrue(outer.evaluate("input"));
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
    assertTrue(outer.evaluate("input"));
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
    assertTrue(level1.evaluate("input"));
  }
}
