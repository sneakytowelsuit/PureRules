package com.github.sneakytowelsuit.purerules.conditions;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.context.EngineContext;
import java.util.Collections;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleTest {
  private static final Long THREAD_ID = 1L;

  @BeforeEach
  void resetEngineContext() {
    EngineContext.getInstance().flushAll();
  }

  @Test
  void testEvaluateReturnsTrue() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    boolean result = rule.evaluate("hello", Collections.emptyList(), THREAD_ID);
    assertTrue(result);
  }

  @Test
  void testEvaluateReturnsFalse() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysFalseOperator())
            .value(5)
            .build();
    boolean result = rule.evaluate("hello", Collections.emptyList(), THREAD_ID);
    assertFalse(result);
  }

  @Test
  void testEvaluateWithNullOperatorThrows() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(null)
            .value(5)
            .build();
    assertThrows(
        AssertionError.class, () -> rule.evaluate("hello", Collections.emptyList(), THREAD_ID));
  }

  @Test
  void testEvaluateWithNullFieldThrows() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(null)
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    assertThrows(
        AssertionError.class, () -> rule.evaluate("hello", Collections.emptyList(), THREAD_ID));
  }

  @Test
  void testEvaluateWithNullFieldValueFunctionThrows() {
    Field<String, Integer> field =
        new Field<String, Integer>() {
          @Override
          public Function<String, Integer> getFieldValueFunction() {
            return null;
          }
        };
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(field)
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    assertThrows(
        AssertionError.class, () -> rule.evaluate("hello", Collections.emptyList(), THREAD_ID));
  }

  @Test
  void testEvaluateWithNullThreadIdThrows() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    assertThrows(AssertionError.class, () -> rule.evaluate("hello", Collections.emptyList(), null));
  }

  @Test
  void testEvaluateWithExceptionInFieldFunction() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.ExceptionField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    assertThrows(RuntimeException.class, () -> rule.evaluate("hello", Collections.emptyList(), 1L));
  }

  @Test
  void testEvaluateWithNullParentIdPath() {
    Rule<String, Integer> rule =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    // Should not throw, parentIdPath can be null
    assertTrue(rule.evaluate("hello", null, 1L));
  }

  @Test
  void testRuleIdIsUnique() {
    Rule<String, Integer> rule1 =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    Rule<String, Integer> rule2 =
        Rule.<String, Integer>builder()
            .field(new TestUtils.DummyField())
            .operator(new TestUtils.AlwaysTrueOperator())
            .value(5)
            .build();
    assertNotEquals(rule1.getId(), rule2.getId());
    assertTrue(rule1.getId().startsWith("rule-"));
  }
}
