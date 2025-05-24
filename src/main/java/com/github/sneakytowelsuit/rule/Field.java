package com.github.sneakytowelsuit.rule;

import java.util.function.Function;

public interface Field<TInput, TValue> {
  Function<TInput, TValue> getFieldValueFunction();
}
