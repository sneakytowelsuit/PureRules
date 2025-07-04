package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

public class LessThanOperator<T extends Comparable<T>> implements Operator<T> {
  @Override
  public boolean test(T input, T value) {
    if (input == null || value == null) {
      return false;
    }
    return input.compareTo(value) < 0;
  }
}
