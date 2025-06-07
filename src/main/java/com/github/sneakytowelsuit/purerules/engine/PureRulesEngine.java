package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContext;
import com.github.sneakytowelsuit.purerules.context.EngineContextImpl;
import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class PureRulesEngine<TInput> implements Closeable {
  private final EngineContext context;
  private final List<RuleGroup<TInput>> ruleGroups;

  /**
   * The mode of the engine, which can be either DETERMINISTIC or PROBABILISTIC. This determines how
   * the rules are evaluated and the results are combined.
   *
   * <ul>
   *   <li>DETERMINISTIC: The engine evaluates rules and returns a boolean result based on the
   *       conditions defined in the rule groups.
   *   <li>PROBABILISTIC: The engine evaluates rules and returns a probability score based on the
   *       conditions defined in the rule groups.
   * </ul>
   */
  private final EngineMode engineMode;

  /**
   * The minimum probability threshold for the PROBABILISTIC engine mode. If the calculated
   * probability is below this threshold, the result will be considered false.
   */
  private Float minimumProbabilityThreshold;

  public static <T> PureRulesEngine<T> getProbablisticEngine(
      Float minimumProbabilityThreshold, List<RuleGroup<T>> ruleGroups) {
    return new PureRulesEngine<>(minimumProbabilityThreshold, ruleGroups);
  }

  /**
   * Creates a new instance of PureRulesEngine with the specified rule groups and minimum
   * probability threshold for PROBABILISTIC mode.
   *
   * @param minimumProbabilityThreshold - The minimum probability threshold for the PROBABILISTIC
   *     engine mode.
   * @param ruleGroups - The list of rule groups to be evaluated by the engine.
   */
  private PureRulesEngine(Float minimumProbabilityThreshold, List<RuleGroup<TInput>> ruleGroups) {
    this.ruleGroups = ruleGroups;
    this.engineMode = EngineMode.PROBABILISTIC;
    this.minimumProbabilityThreshold = minimumProbabilityThreshold;
    this.context = new EngineContextImpl();
  }

  public static <T> PureRulesEngine<T> getDeterministicEngine(List<RuleGroup<T>> ruleGroups) {
    return new PureRulesEngine<>(ruleGroups);
  }

  private PureRulesEngine(List<RuleGroup<TInput>> ruleGroups) {
    this.ruleGroups = ruleGroups;
    this.engineMode = EngineMode.DETERMINISTIC;
    this.context = new EngineContextImpl();
  }

  public Map<String, Boolean> evaluate(TInput input) {
    Map<String, Boolean> resultMap =
        this.getRuleGroups().stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    RuleGroup::getId, ruleGroup -> ruleGroup.evaluate(input)));
    this.getContext().flushEvaluationContext(this.getEngineMode());
    return resultMap;
  }

  @Override
  public void close() {
    // Ensure that the evaluation context is flushed when the engine is closed
    for (EngineMode mode : EngineMode.values()) {
      this.getContext().flushEvaluationContext(mode);
    }
  }
}
