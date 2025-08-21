package com.github.sneakytowelsuit.purerules.conditions;

// spotless:off
/**
 * Defines how multiple conditions within a {@link RuleGroup} are logically combined.
 *
 * <p>The combinator determines the evaluation strategy for rule groups containing multiple
 * conditions (rules or nested rule groups):
 *
 * <ul>
 *   <li>{@link #AND}: All conditions must evaluate to true for the group to pass</li>
 *   <li>{@link #OR}: At least one condition must evaluate to true for the group to pass</li>
 * </ul>
 *
 * <p>The combinator works in conjunction with the group's inversion flag - the combined result can
 * be inverted after applying the combinator logic.
 *
 * <p><strong>Examples:</strong>
 * <pre>{@code
 * // AND: person must be both adult AND US citizen
 * RuleGroup<Person> adultUSCitizen = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .conditions(Arrays.asList(ageRule, citizenshipRule))
 *     .build();
 *
 * // OR: person can be either veteran OR senior citizen  
 * RuleGroup<Person> veteranOrSenior = RuleGroup.<Person>builder()
 *     .combinator(Combinator.OR)
 *     .conditions(Arrays.asList(veteranRule, seniorRule))
 *     .build();
 * }</pre>
 *
 * @see RuleGroup
 * @see Bias
 */
// spotless:on
public enum Combinator {
  // spotless:off
  /**
   * AND combinator - requires all conditions in the group to evaluate to true. If any condition
   * fails, the entire group fails.
   */
  // spotless:on
  AND,

  // spotless:off
  /**
   * OR combinator - requires at least one condition in the group to evaluate to true. The group
   * succeeds if any condition passes.
   */
  // spotless:on
  OR;
}
