package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextValue;
import com.github.sneakytowelsuit.purerules.evaluation.DeterministicEvaluationService;
import com.github.sneakytowelsuit.purerules.evaluation.EvaluationService;
import com.github.sneakytowelsuit.purerules.evaluation.ProbabilisticEvaluationService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PureRulesEngine<TInput, TInputId> {
  private final List<Condition<TInput>> conditions;
    /**
   * The evaluation service used to evaluate the rules based on the engine mode. This service
   * encapsulates the logic for evaluating conditions and combining results.
   */
  private final EvaluationService<TInput, TInputId> evaluationService;

  private final EngineContextService<TInput, TInputId> engineContextService;

  public static <T, I> PureRulesEngine<T, I> getProbabilisticEngine(
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
      /**
       * The minimum probability threshold for the PROBABILISTIC engine mode. If the calculated
       * probability is below this threshold, the result will be considered false.
       */
      this.evaluationService =
        new ProbabilisticEvaluationService<>(conditions, minimumProbabilityThreshold);
    this.engineContextService = new EngineContextService<>(inputIdGetter);
  }

  public static <T, I> PureRulesEngine<T, I> getDeterministicEngine(
      Function<T, I> inputIdGetter, List<Condition<T>> conditions) {
    return new PureRulesEngine<>(inputIdGetter, conditions);
  }

  private PureRulesEngine(
      Function<TInput, TInputId> inputIdGetter, List<Condition<TInput>> conditions) {
    this.conditions = conditions;
    this.evaluationService = new DeterministicEvaluationService<>(conditions);
    this.engineContextService = new EngineContextService<>(inputIdGetter);
  }

  private EvaluationService<TInput, TInputId> getEvaluationService() {
    return this.evaluationService;
  }

  private EngineContextService<TInput, TInputId> getEngineContextService() {
    return this.engineContextService;
  }

  public Map<String, Boolean> evaluate(TInput input) {
    Map<String, Boolean> results = this.getEvaluationService().evaluate(input, this.getEngineContextService());
    if (results == null) {
      return Collections.emptyMap();
    }
    // Clear the context after evaluation to avoid memory leaks
    this.getEngineContextService().flush(input);
    return results;
  }

  public Map<TInputId, Map<String, Boolean>> evaluateAll(List<TInput> inputs) {
    return inputs.stream()
        .collect(
            Collectors.toMap(
                input -> this.engineContextService.getInputIdGetter().apply(input),
                this::evaluate));
  }
}
