package com.github.sneakytowelsuit.purerules;

import lombok.*;

@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public final class Rule<TInput, TValue> implements Condition<TInput> {
  private final Field<TInput, TValue> field;
  private final Operator<TInput, TValue> operator;
  private final TValue value;

  public boolean test(TInput input) {
    return this.getOperator().test(input, this.getField(), this.getValue());
  }
}
