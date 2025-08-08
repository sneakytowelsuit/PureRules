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

  public void flushAll() {
    // Clear the condition evaluation context to reset state for the next evaluation
    conditionEvaluationContext.getConditionContextMap().clear();

    // Clear the field context to remove cached field values
    fieldContext.getFieldContextMap().clear();
  }
}
