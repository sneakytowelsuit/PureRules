package com.github.sneakytowelsuit.rule;

import lombok.*;

@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
public final class Rule<TInput, TValue> implements Evaluator<TInput> {
  private final Field<TInput, TValue> field;
  private final Operator<TInput, TValue> operator;
  private final TValue value;

  public boolean test(TInput input) {
    return this.getOperator().test(input, this.getField(), this.getValue());
  }
}
