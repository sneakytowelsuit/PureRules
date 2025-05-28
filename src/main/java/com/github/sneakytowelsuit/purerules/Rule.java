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
  private final Operator<TValue> operator;
  private final TValue value;

  public boolean test(TInput input) {
      assert this.getOperator() != null;
      assert this.getField() != null;
      assert this.getField().getFieldValueFunction() != null;
      return this.getOperator().test(this.getField().getFieldValueFunction().apply(input), this.getValue());
  }
}
