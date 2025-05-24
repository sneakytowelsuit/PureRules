package com.github.sneakytowelsuit.rule;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class Rule<TInput, TValue> implements Evaluator<TInput> {
  private final Field<TInput, TValue> field;
  private final Operator<TInput, TValue> operator;
  private final Value<TValue> value;

  public boolean test(TInput input) {
    return this.getOperator().test(this.getField(), this.getValue());
  }
}
