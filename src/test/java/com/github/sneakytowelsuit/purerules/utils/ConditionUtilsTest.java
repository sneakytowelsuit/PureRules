package com.github.sneakytowelsuit.purerules.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConditionUtilsTest {

  @Test
  void testGetIdPathWithNullCurrentPath() {
    Rule<String, Integer> rule = TestUtils.alwaysTrueRule();
    List<String> result = ConditionUtils.getIdPath(rule, null);
    assertEquals(Collections.singletonList(rule.getId()), result);
  }

  @Test
  void testGetIdPathWithEmptyCurrentPath() {
    Rule<String, Integer> rule = TestUtils.alwaysTrueRule();
    List<String> result = ConditionUtils.getIdPath(rule, Collections.emptyList());
    assertEquals(Collections.singletonList(rule.getId()), result);
  }

  @Test
  void testGetIdPathWithNonEmptyCurrentPath() {
    Rule<String, Integer> rule = TestUtils.alwaysTrueRule();
    List<String> current = Arrays.asList("root", "child");
    List<String> result = ConditionUtils.getIdPath(rule, current);
    assertEquals(Arrays.asList("root", "child", rule.getId()), result);
  }

  @Test
  void testGetIdPathDoesNotMutateInputList() {
    Rule<String, Integer> rule = TestUtils.alwaysTrueRule();
    List<String> current = Arrays.asList("a", "b");
    List<String> copy = Arrays.asList("a", "b");
    ConditionUtils.getIdPath(rule, current);
    assertEquals(copy, current);
  }
}
