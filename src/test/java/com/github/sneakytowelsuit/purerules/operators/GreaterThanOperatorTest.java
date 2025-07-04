package com.github.sneakytowelsuit.purerules.operators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GreaterThanOperatorTest {
  private final GreaterThanOperator<Integer> intOperator = new GreaterThanOperator<>();
  private final GreaterThanOperator<String> stringOperator = new GreaterThanOperator<>();

  @Test
  void testIntegerGreaterThan() {
    assertTrue(intOperator.test(5, 3));
    assertFalse(intOperator.test(3, 5));
    assertFalse(intOperator.test(5, 5));
    assertFalse(intOperator.test(null, 1));
    assertFalse(intOperator.test(1, null));
  }

  @Test
  void testStringGreaterThan() {
    assertTrue(stringOperator.test("b", "a"));
    assertFalse(stringOperator.test("a", "b"));
    assertFalse(stringOperator.test("a", "a"));
  }
}
