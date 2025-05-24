package com.github.sneakytowelsuit.rule;

import java.util.function.Function;

public final class Field<TInput, TValue> {

  public TValue getFieldValue(TInput input, Function<TInput, TValue> valueGetter) {
    return valueGetter.apply(input);
  }
}
