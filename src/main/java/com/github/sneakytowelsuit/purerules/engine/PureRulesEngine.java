package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.evaluation.EvaluationService;

import java.util.List;
import java.util.Map;

public class PureRulesEngine<TInput> {
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

  /**
   * The evaluation service used to evaluate the rules based on the engine mode. This service
   * encapsulates the logic for evaluating conditions and combining results.
   */
  private EvaluationService<TInput> evaluationService;

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
  }

  public static <T> PureRulesEngine<T> getDeterministicEngine(List<RuleGroup<T>> ruleGroups) {
    return new PureRulesEngine<>(ruleGroups);
  }

  private PureRulesEngine(List<RuleGroup<TInput>> ruleGroups) {
    this.ruleGroups = ruleGroups;
    this.engineMode = EngineMode.DETERMINISTIC;
  }

  private EngineMode getEngineMode() {
    return this.engineMode;
  }

  private EvaluationService<TInput> getEvaluationService() {
    return this.evaluationService;
  }
  public Map<String, Boolean> evaluate(TInput input) {
    return this.getEvaluationService().evaluate(input);
  }
}
