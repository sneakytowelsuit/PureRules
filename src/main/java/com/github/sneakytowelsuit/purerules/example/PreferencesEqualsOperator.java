package com.github.sneakytowelsuit.purerules.example;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

public class PreferencesEqualsOperator implements Operator<Preferences> {
  @Override
  public boolean test(Preferences input, Preferences value) {
    return input.equals(value);
  }
}
