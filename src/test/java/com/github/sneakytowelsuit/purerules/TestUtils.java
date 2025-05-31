package com.github.sneakytowelsuit.purerules;

import com.github.sneakytowelsuit.purerules.conditions.Field;
import com.github.sneakytowelsuit.purerules.conditions.Operator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import java.util.function.Function;

public class TestUtils {
  public static class DummyField implements Field<String, Integer> {
    @Override
    public Function<String, Integer> getFieldValueFunction() {
      return String::length;
    }
  }

  public static class ExceptionField implements Field<String, Integer> {
    @Override
    public Function<String, Integer> getFieldValueFunction() {
      return s -> {
        throw new RuntimeException("Field error");
      };
    }
  }

  public static class AlwaysTrueOperator implements Operator<Integer> {
    @Override
    public boolean test(Integer a, Integer b) {
      return true;
    }
  }

  public static class AlwaysFalseOperator implements Operator<Integer> {
    @Override
    public boolean test(Integer a, Integer b) {
      return false;
    }
  }

  public static Rule<String, Integer> alwaysTrueRule() {
    return Rule.<String, Integer>builder()
        .field(new DummyField())
        .operator(new AlwaysTrueOperator())
        .value(null)
        .build();
  }

  public static Rule<String, Integer> alwaysFalseRule() {
    return Rule.<String, Integer>builder()
        .field(new DummyField())
        .operator(new AlwaysFalseOperator())
        .value(null)
        .build();
  }
}
