package com.github.sneakytowelsuit.purerules;

public interface Operator<TInput, TValue> {
  boolean test(TInput input, Field<TInput, TValue> field, TValue value);
}
