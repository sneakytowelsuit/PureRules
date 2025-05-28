package com.github.sneakytowelsuit.purerules;

import java.util.function.Function;

public interface Field<TInput, TValue> {
  Function<TInput, TValue> getFieldValueFunction();
}
