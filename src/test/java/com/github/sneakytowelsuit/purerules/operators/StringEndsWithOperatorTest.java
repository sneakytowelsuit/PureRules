package com.github.sneakytowelsuit.purerules.operators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringEndsWithOperatorTest {
  private final StringEndsWithOperator operator = new StringEndsWithOperator();

  @Test
  void testEndsWith() {
    assertTrue(operator.test("hello world", "world"));
    assertFalse(operator.test("hello world", "hello"));
    assertTrue(operator.test("test", "t"));
    assertFalse(operator.test("test", "T"));
    assertFalse(operator.test(null, "test"));
    assertFalse(operator.test("test", null));
    assertFalse(operator.test(null, null));
  }
}
