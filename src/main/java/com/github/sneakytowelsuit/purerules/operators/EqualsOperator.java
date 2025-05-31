package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

public class EqualsOperator<T> implements Operator<T> {
  @Override
  public boolean test(T input, T value) {
    if (input == null && value == null) {
      return true; // Both are null, considered equal
    }
    if (input == null || value == null) {
      return false; // One is null, the other is not, considered not equal
    }
    return input.equals(value);
  }
}
