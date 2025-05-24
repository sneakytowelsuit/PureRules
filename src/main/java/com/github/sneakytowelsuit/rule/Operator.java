package com.github.sneakytowelsuit.rule;

public interface Operator<TInput, TValue> {
  boolean test(TInput input, Field<TInput, TValue> field, Value<TValue> value);
}
