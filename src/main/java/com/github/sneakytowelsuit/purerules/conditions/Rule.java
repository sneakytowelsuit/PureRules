package com.github.sneakytowelsuit.purerules.conditions;

import lombok.*;

import java.util.UUID;

/**
 * Represents a single rule condition that evaluates an input using a field extractor, an operator,
 * and a comparison value. The rule is uniquely identified and can be evaluated in the context of a
 * rule group or independently.
 *
 * @param <TInput> the type of input to evaluate
 * @param <TValue> the type of value extracted and compared
 */
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public final class Rule<TInput, TValue> implements Condition<TInput> {
  private static final String RULE_ID_PREFIX = "rule-";
  @Builder.Default private final String id = RULE_ID_PREFIX + UUID.randomUUID().toString();
  private final Field<TInput, TValue> field;
  private final Operator<TValue> operator;
  private final TValue value;
  @Builder.Default private final Integer priority = 1;

  /**
   * Evaluates this rule against the given input, using the provided parent ID path and thread ID.
   * Stores the result in the deterministic evaluation context.
   *
   * @param input the input to evaluate
   * @return true if the rule condition is satisfied, false otherwise
   * @throws AssertionError if required fields or parameters are null
   */
  public boolean evaluate(
      TInput input
  ) {
    assert this.getOperator() != null;
    assert this.getField() != null;
    assert this.getField().getFieldValueFunction() != null;

    TValue fieldValue = this.getField().getFieldValueFunction().apply(input);
    TValue valueValue = this.getValue();
    return this.getOperator().test(fieldValue, valueValue);
  }
}
