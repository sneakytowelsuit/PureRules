package com.github.sneakytowelsuit.purerules.conditions;

import java.util.UUID;
import lombok.*;

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
  @Builder.Default private final Integer weight = 1;

  @Override
  public Integer getCumulativeWeight() {
    return this.getWeight();
  }
}
