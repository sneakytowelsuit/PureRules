package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.evaluation.DeterministicEvaluationService;
import com.github.sneakytowelsuit.purerules.evaluation.EvaluationService;
import com.github.sneakytowelsuit.purerules.evaluation.ProbabilisticEvaluationService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PureRulesEngine<TInput, TInputId> {
  private final List<Condition<TInput>> conditions;

  /**
   * The mode of the engine, which can be either DETERMINISTIC or PROBABILISTIC. This determines how
   * the rules are evaluated and the results are combined.
   *
   * <ul>
   *   <li>{@link EngineMode#DETERMINISTIC}: The engine evaluates rules and returns a boolean result
   *       based on the conditions defined in the rule groups.
   *   <li>{@link EngineMode#PROBABILISTIC}: The engine evaluates rules and returns a probability
   *       score based on the conditions defined in the rule groups.
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
  private final EvaluationService<TInput, TInputId> evaluationService;

  private Function<TInput, TInputId> inputIdGetter;
  private EngineContextService<TInputId> engineContextService;

  public static <T, I> PureRulesEngine<T, I> getProbablisticEngine(
      Function<T, I> inputIdGetter,
      Float minimumProbabilityThreshold,
      List<Condition<T>> conditions) {
    return new PureRulesEngine<>(inputIdGetter, minimumProbabilityThreshold, conditions);
  }

  /**
   * Creates a new instance of PureRulesEngine with the specified rule groups and minimum
   * probability threshold for PROBABILISTIC mode.
   *
   * @param minimumProbabilityThreshold - The minimum probability threshold for the PROBABILISTIC
   *     engine mode.
   * @param conditions - The list of conditions to be evaluated by the engine.
   */
  private PureRulesEngine(
      Function<TInput, TInputId> inputIdGetter,
      Float minimumProbabilityThreshold,
      List<Condition<TInput>> conditions) {
    this.conditions = conditions;
    this.engineMode = EngineMode.PROBABILISTIC;
    this.minimumProbabilityThreshold = minimumProbabilityThreshold;
    this.evaluationService = new ProbabilisticEvaluationService<>(conditions, minimumProbabilityThreshold);
    this.inputIdGetter = inputIdGetter;
    this.engineContextService = new EngineContextService<>(EngineMode.PROBABILISTIC);
  }

  public static <T, I> PureRulesEngine<T, I> getDeterministicEngine(
      Function<T, I> inputIdGetter, List<Condition<T>> conditions) {
    return new PureRulesEngine<>(inputIdGetter, conditions);
  }

  private PureRulesEngine(
      Function<TInput, TInputId> inputIdGetter, List<Condition<TInput>> conditions) {
    this.conditions = conditions;
    this.engineMode = EngineMode.DETERMINISTIC;
    this.evaluationService = new DeterministicEvaluationService<>(conditions);
    this.inputIdGetter = inputIdGetter;
    this.engineContextService = new EngineContextService<>(EngineMode.DETERMINISTIC);
  }

  private EvaluationService<TInput,TInputId> getEvaluationService() {
    return this.evaluationService;
  }

  private EngineContextService<TInputId> getEngineContextService() {
    return this.engineContextService;
  }

  public Map<String, Boolean> evaluate(TInput input) {
    // Set the input ID in the engine context service
    this.getEngineContextService().setInputId(this.inputIdGetter, input);
    return this.getEvaluationService().evaluate(input, this.getEngineContextService());
  }
}
