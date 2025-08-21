package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import java.util.Map;

/**
 * Core evaluation service interface that defines how rules and conditions are processed against
 * input data in the PureRules engine.
 *
 * <p>This interface abstracts the evaluation strategy, allowing for different implementations such
 * as deterministic boolean evaluation or probabilistic scoring. The service works in conjunction
 * with an {@link EngineContextService} to maintain evaluation context and field values.
 *
 * <p>Implementations should handle:
 *
 * <ul>
 *   <li>Processing individual rules and rule groups
 *   <li>Applying appropriate evaluation logic (deterministic vs probabilistic)
 *   <li>Managing evaluation context and caching field values
 *   <li>Returning results in a consistent format
 * </ul>
 *
 * @param <TInput> the type of input data to be evaluated against the rules
 * @param <TInputId> the type used to uniquely identify input instances for context management
 */
public interface IEvaluationService<TInput, TInputId> {

  /**
   * Evaluates all configured conditions against the provided input and returns a map of condition
   * results.
   *
   * <p>This method processes each condition (rule or rule group) defined in the service and
   * evaluates it against the input data. The evaluation process uses the provided context service
   * to cache field values and maintain evaluation state.
   *
   * @param input the input data to evaluate against the configured conditions
   * @param engineContextService the context service for managing field values and evaluation state
   * @return a map where keys are condition IDs and values are boolean results indicating whether
   *     each condition was satisfied by the input
   */
  public Map<String, Boolean> evaluate(
      TInput input, EngineContextService<TInput, TInputId> engineContextService);

  /**
   * Traces the evaluation process for the given input, calculating the context and field values
   * without returning a result map. This method is useful for debugging or logging the evaluation
   * process.
   *
   * @param input the input data to trace the evaluation for
   * @param engineContextService the context service for managing field values and evaluation state
   */
  public void trace(TInput input, EngineContextService<TInput, TInputId> engineContextService);
}
