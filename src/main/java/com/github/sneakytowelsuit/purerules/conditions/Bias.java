package com.github.sneakytowelsuit.purerules.conditions;

import lombok.Getter;

/**
 * Specifies the default result (bias) for a RuleGroup when it contains no conditions.
 *
 * <p>Bias provides a way to handle empty rule groups gracefully by defining their default behavior.
 * This is particularly useful when conditions are dynamically constructed or filtered.
 *
 * <ul>
 *   <li><strong>INCLUSIVE:</strong> evaluates to {@code true} if the group is empty (optimistic)</li>
 *   <li><strong>EXCLUSIVE:</strong> evaluates to {@code false} if the group is empty (pessimistic)</li>
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 * <pre>{@code
 * // Optimistic: assume success when no conditions are present
 * RuleGroup<Person> optionalChecks = RuleGroup.<Person>builder()
 *     .bias(Bias.INCLUSIVE)  // true for empty group
 *     .conditions(dynamicallyFilteredRules)  // might result in empty list
 *     .build();
 *
 * // Pessimistic: require explicit conditions to pass
 * RuleGroup<Person> requiredChecks = RuleGroup.<Person>builder()
 *     .bias(Bias.EXCLUSIVE)  // false for empty group  
 *     .conditions(mandatoryRules)  // should never be empty
 *     .build();
 * }</pre>
 *
 * <p><strong>Practical Application:</strong>
 * Useful in scenarios like feature flags, conditional validation, or dynamic rule loading
 * where the presence or absence of conditions has semantic meaning.
 *
 * @see RuleGroup
 * @see Combinator
 */
@Getter
public enum Bias {
  INCLUSIVE(true),
  EXCLUSIVE(false);

  /** The default result of the RuleGroup when it contains no conditions. */
  private final boolean biasResult;

  Bias(boolean biasResult) {
    this.biasResult = biasResult;
  }
}
