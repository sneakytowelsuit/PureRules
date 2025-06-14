package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.engine.EngineMode;

/**
 * Represents a logical condition that can be evaluated. This interface is sealed and only permits
 * {@link Rule} and {@link RuleGroup} as implementations.
 *
 * @param <InputType> the type of input to evaluate
 */
public sealed interface Condition<InputType> permits Rule, RuleGroup {
  public String getId();

  /**
   * Gets the priority of this condition. Higher priority conditions are weighted heavier when evaluating in
   * probabilistic mode.
   * <ul>
   *     <li>{@link EngineMode#DETERMINISTIC} ignores priority altogether</li>
   *     <li>{@link EngineMode#PROBABILISTIC} respects priority to calculate balance while computing the confidence</li>
   * </ul>
   * @return the priority of this condition, or 0 to represent no priority.
   */
  public Integer getPriority();
}
