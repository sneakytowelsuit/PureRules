package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.context.condition.ConditionContext;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.context.field.FieldContext;
import com.github.sneakytowelsuit.purerules.context.field.FieldContextKey;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;

/**
 * Manages evaluation context for the PureRules engine, including field value caching and condition
 * evaluation state tracking.
 *
 * <p>This service provides centralized context management during rule evaluation, offering:
 *
 * <ul>
 *   <li>Field value caching to avoid redundant field extractions
 *   <li>Condition evaluation state tracking for debugging and analysis
 *   <li>Input identification for context key generation
 * </ul>
 *
 * <p>The context service is essential for performance optimization and evaluation transparency. It
 * ensures that field values are computed only once per input instance and maintains a complete
 * audit trail of evaluation decisions.
 *
 * <p>Context information includes:
 *
 * <ul>
 *   <li>Field values extracted from inputs (cached by field type and input ID)
 *   <li>Rule evaluation results with operator details and field/value pairs
 *   <li>Rule group evaluation results with combinator logic and scoring information
 * </ul>
 *
 * @param <TInput> the type of input data being evaluated
 * @param <TInputId> the type used to uniquely identify input instances
 */
@Getter
public class EngineContextService<TInput, TInputId> {

  /** Context for tracking condition evaluation results and metadata. */
  private final ConditionContext<TInputId> conditionEvaluationContext;

  /** Context for caching extracted field values to improve performance. */
  private final FieldContext<TInputId> fieldContext;

  /** Function to extract unique identifiers from input instances for context management. */
  private final Function<TInput, TInputId> inputIdGetter;

  /**
   * Creates a new engine context service with the specified input ID extraction function.
   *
   * @param inputIdGetter function that extracts a unique identifier from input instances, used for
   *     context key generation and caching
   */
  public EngineContextService(Function<TInput, TInputId> inputIdGetter) {
    this.conditionEvaluationContext = new ConditionContext<>();
    this.fieldContext = new FieldContext<>();
    this.inputIdGetter = inputIdGetter;
  }

  /**
   * Flushes cached context information for a specific input instance.
   *
   * <p>This method removes all cached field values and condition evaluation context associated
   * with the specified input instance. This is typically called automatically after evaluation
   * to prevent memory leaks, but can also be used manually for fine-grained context management.
   *
   * <p><strong>What gets flushed:</strong>
   * <ul>
   *   <li>All field values cached for this input ID</li>
   *   <li>All condition evaluation results for this input ID</li>
   *   <li>Associated metadata and timing information</li>
   * </ul>
   *
   * <p><strong>Usage Example:</strong>
   * <pre>{@code
   * // Evaluate multiple people
   * Map<String, Boolean> results1 = engine.evaluate(person1);
   * Map<String, Boolean> results2 = engine.evaluate(person2);
   * 
   * // Context for person1 and person2 is automatically flushed after each evaluation
   * 
   * // Manual flush for specific person (advanced usage)
   * contextService.flush(person1);  // Only person1's context is cleared
   * }</pre>
   *
   * @param input the input instance whose context should be flushed
   */
  public void flush(TInput input) {
    List<ConditionContextKey<TInputId>> conditionContextKeysToRemove =
        conditionEvaluationContext.getConditionContextMap().keySet().stream()
            .filter(key -> key.inputId().equals(inputIdGetter.apply(input)))
            .toList();
    List<FieldContextKey<TInputId>> fieldContextKeysToRemove =
        fieldContext.getFieldContextMap().keySet().stream()
            .filter(key -> key.inputId().equals(inputIdGetter.apply(input)))
            .toList();

    // Remove condition context keys associated with the input ID
    conditionContextKeysToRemove.forEach(
        conditionEvaluationContext.getConditionContextMap()::remove);
    // Remove field context keys associated with the input ID
    fieldContextKeysToRemove.forEach(fieldContext.getFieldContextMap()::remove);
  }

  /**
   * Clears all cached context information for all input instances.
   *
   * <p>This method provides a complete reset of the context service, removing all cached field
   * values and condition evaluation context for every input instance that has been processed.
   * Use this method when you need to completely clear the context state.
   *
   * <p><strong>When to use:</strong>
   * <ul>
   *   <li>Between different evaluation sessions</li>
   *   <li>When switching to evaluate a completely different set of inputs</li>
   *   <li>For memory cleanup in long-running applications</li>
   *   <li>During testing to ensure clean state between test cases</li>
   * </ul>
   *
   * <p><strong>Performance Note:</strong>
   * Clearing all context will require field values to be re-extracted on the next evaluation,
   * which may temporarily impact performance until the cache is rebuilt.
   *
   * <p><strong>Usage Example:</strong>
   * <pre>{@code
   * // Process a batch of evaluations
   * for (Person person : largeBatch) {
   *     Map<String, Boolean> results = engine.evaluate(person);
   *     processResults(results);
   * }
   * 
   * // Clear all context before processing next batch
   * contextService.flushAll();
   * 
   * // Process next batch with clean context
   * for (Person person : nextBatch) {
   *     Map<String, Boolean> results = engine.evaluate(person);
   *     processResults(results);
   * }
   * }</pre>
   */
  public void flushAll() {
    // Clear the condition evaluation context to reset state for the next evaluation
    conditionEvaluationContext.getConditionContextMap().clear();

    // Clear the field context to remove cached field values
    fieldContext.getFieldContextMap().clear();
  }
}
