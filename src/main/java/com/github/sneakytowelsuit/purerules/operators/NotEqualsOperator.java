package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

public class NotEqualsOperator<T> implements Operator<T> {
  @Override
  public boolean test(T input, T value) {
    if (input == null && value == null) {
      return false;
    }
    if (input == null || value == null) {
      return true;
    }
    return !input.equals(value);
  }
}
