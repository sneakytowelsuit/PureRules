package com.github.sneakytowelsuit.purerules.conditions;

import lombok.Getter;

/**
 * Specifies the default result (bias) for a RuleGroup when it contains no conditions.
 *
 * <ul>
 *   <li>INCLUSIVE: evaluates to {@code true} if the group is empty.
 *   <li>EXCLUSIVE: evaluates to {@code false} if the group is empty.
 * </ul>
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
