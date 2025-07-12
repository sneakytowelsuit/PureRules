package com.github.sneakytowelsuit.purerules.conditions;

/**
 * Defines how multiple conditions within a {@link RuleGroup} are logically combined.
 *
 * <p>The combinator determines the evaluation strategy for rule groups containing multiple
 * conditions (rules or nested rule groups):
 *
 * <ul>
 *   <li>{@link #AND}: All conditions must evaluate to true for the group to pass
 *   <li>{@link #OR}: At least one condition must evaluate to true for the group to pass
 * </ul>
 *
 * <p>The combinator works in conjunction with the group's inversion flag - the combined result can
 * be inverted after applying the combinator logic.
 */
public enum Combinator {
  /**
   * AND combinator - requires all conditions in the group to evaluate to true. If any condition
   * fails, the entire group fails.
   */
  AND,

  /**
   * OR combinator - requires at least one condition in the group to evaluate to true. The group
   * succeeds if any condition passes.
   */
  OR;
}
