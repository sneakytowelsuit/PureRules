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
   * Gets the priority of this condition. Higher priority conditions are weighted heavier when
   * evaluating in probabilistic mode.
   *
   * <ul>
   *   <li>{@link EngineMode#DETERMINISTIC} ignores priority altogether
   *   <li>{@link EngineMode#PROBABILISTIC} respects priority to calculate balance while computing
   *       the confidence
   * </ul>
   *
   * @return the priority of this condition, or 1 to represent baseline priority.
   */
  public Integer getWeight();
}
