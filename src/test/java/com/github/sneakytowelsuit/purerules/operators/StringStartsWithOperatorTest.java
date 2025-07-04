package com.github.sneakytowelsuit.purerules.operators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringStartsWithOperatorTest {
  private final StringStartsWithOperator operator = new StringStartsWithOperator();

  @Test
  void testStartsWith() {
    assertTrue(operator.test("hello world", "hello"));
    assertFalse(operator.test("hello world", "world"));
    assertTrue(operator.test("test", "t"));
    assertFalse(operator.test("test", "T"));
    assertFalse(operator.test(null, "test"));
    assertFalse(operator.test("test", null));
    assertFalse(operator.test(null, null));
  }
}
