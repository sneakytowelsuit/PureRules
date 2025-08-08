package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextValue;
import com.github.sneakytowelsuit.purerules.context.condition.RuleContextValue;
import com.github.sneakytowelsuit.purerules.context.condition.RuleGroupContextValue;
import com.github.sneakytowelsuit.purerules.context.field.FieldContextKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EvaluationService} that performs probabilistic evaluation of rules and
 * conditions using weighted scoring and probability thresholds.
 *
 * <p>This service evaluates conditions by calculating probability scores based on weighted
 * contributions from individual rules and rule groups. Key features include:
 *
 * <ul>
 *   <li>Weighted scoring: Rules and rule groups contribute to the overall score based on their
 *       configured weights
 *   <li>Probability thresholding: Results are converted to boolean values by comparing calculated
 *       probabilities against a minimum threshold
 *   <li>Recursive evaluation: Nested rule groups are evaluated recursively with proper weight
 *       propagation
 *   <li>Context preservation: Maintains detailed evaluation context for debugging and analysis
 * </ul>
 *
 * <p>The probabilistic evaluation differs from deterministic evaluation by considering partial
 * matches and confidence levels rather than strict boolean logic.
 *
 * @param <TInput> the type of input data to be evaluated
 * @param <TInputId> the type used to uniquely identify input instances
 */
public class ProbabilisticEvaluationService<TInput, TInputId>
    implements EvaluationService<TInput, TInputId> {
  /**
   * The minimum probability threshold for considering a condition as passing. This is applied at
   * the root of each condition in the list of conditions provided
   */
  private final Float minimumProbability;

  /** The list of conditions to evaluate against input data. */
  private final List<Condition<TInput>> conditions;

  /**
   * Creates a new probabilistic evaluation service with the specified conditions and minimum
   * probability threshold.
   *
   * @param conditions the list of conditions (rules and rule groups) to evaluate
   * @param minimumProbability the minimum probability threshold (0.0 to 1.0) required for a
   *     condition to be considered as passing
   */
  public ProbabilisticEvaluationService(
      List<Condition<TInput>> conditions, Float minimumProbability) {
    this.conditions = conditions;
    this.minimumProbability = minimumProbability;
  }

  /**
   * Validates and returns the minimum probability threshold.
   *
   * @return the validated minimum probability threshold
   * @throws IllegalArgumentException if the minimum probability is null or outside the range [0, 1]
   */
  private Float getMinimumProbability() {
    if (this.minimumProbability == null
        || this.minimumProbability < 0f
        || this.minimumProbability > 1f) {
      throw new IllegalArgumentException("Minimum probability must be between 0 and 1");
    }
    return this.minimumProbability;
  }

  /**
   * Evaluates all configured conditions against the input using probabilistic scoring.
   *
   * <p>Each condition is evaluated to produce a probability score, which is then compared against
   * the minimum probability threshold to determine the final boolean result. The evaluation process
   * considers weights and uses recursive scoring for nested rule groups.
   *
   * @param input the input data to evaluate
   * @param engineContextService the context service for caching and state management
   * @return a map of condition IDs to their boolean evaluation results (after threshold
   *     application)
   */
  @Override
  public Map<String, Boolean> evaluate(
      TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    return this.conditions.stream()
        .collect(
            Collectors.toMap(
                Condition::getId,
                condition ->
                    evaluateCondition(
                        input, condition, engineContextService, this.getMinimumProbability())));
  }

  /**
   * Evaluates a single condition, dispatching to the appropriate evaluation method based on the
   * condition type.
   *
   * @param input the input data to evaluate
   * @param condition the condition to evaluate (either a Rule or RuleGroup)
   * @param engineContextService the context service for state management
   * @param minProbability the minimum probability threshold for boolean conversion
   * @return the boolean result of the condition evaluation
   */
  private boolean evaluateCondition(
      TInput input,
      Condition<TInput> condition,
      EngineContextService<TInput, TInputId> engineContextService,
      float minProbability) {
    return switch (condition) {
      case Rule<TInput, ?> rule -> evaluateRule(input, rule, engineContextService);
      case RuleGroup<TInput> ruleGroup ->
          evaluateRuleGroup(input, ruleGroup, engineContextService, minProbability);
    };
  }

  /**
   * Evaluates a single rule against the input and stores the result in the evaluation context.
   *
   * <p>In probabilistic evaluation, individual rules still produce boolean results, but their
   * weights are considered when combining results in rule groups. The rule's weight is stored in
   * the context for use in probability calculations.
   *
   * @param <V> the type of value that the rule operates on
   * @param input the input to evaluate against the rule
   * @param rule the rule to evaluate
   * @param engineContextService the context service that provides access to field and condition
   *     evaluation contexts
   * @return true if the rule evaluates to true, false otherwise
   */
  private <V> boolean evaluateRule(
      TInput input,
      Rule<TInput, V> rule,
      EngineContextService<TInput, TInputId> engineContextService) {
    if (rule == null) {
      // Default to false if the rule is null
      return false;
    }
    assert rule.getField() != null;
    assert rule.getOperator() != null;
    assert rule.getValue() != null;

    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V ruleValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, ruleValue);
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            new ConditionContextKey<>(
                engineContextService.getInputIdGetter().apply(input), rule.getId()),
            RuleContextValue.builder()
                .id(rule.getId())
                .operator(rule.getOperator().getClass().getName())
                .result(result ? 1 : 0)
                // Rules are weighed based solely on their weight since they are leaf nodes
                .maximumResult(rule.getWeight())
                .build());
    return result;
  }

  /**
   * Extracts and caches the field value from the input for the specified rule.
   *
   * <p>This method uses the context service to cache field values, ensuring that the same field is
   * only extracted once per input instance, improving performance when multiple rules use the same
   * field.
   *
   * @param <V> the type of value extracted by the field
   * @param input the input data to extract the field value from
   * @param rule the rule containing the field extractor
   * @param engineContextService the context service for caching field values
   * @return the extracted field value
   */
  private <V> V getFieldValue(
      TInput input,
      Rule<TInput, V> rule,
      EngineContextService<TInput, TInputId> engineContextService) {
    TInputId inputId = engineContextService.getInputIdGetter().apply(input);
    return (V)
        engineContextService
            .getFieldContext()
            .getFieldContextMap()
            .computeIfAbsent(
                new FieldContextKey<>(inputId, rule.getField().getClass().getName()),
                _ignored -> rule.getField().getFieldValueFunction().apply(input));
  }

  /**
   * Evaluates a rule group using probabilistic scoring with weighted contributions from member
   * conditions.
   *
   * <p>This method calculates a probability score by:
   *
   * <ol>
   *   <li>Evaluating each condition in the group (rules and nested rule groups)
   *   <li>Collecting weighted results from all conditions
   *   <li>Calculating the overall probability as the ratio of achieved score to maximum possible
   *       score
   *   <li>Applying the group's own weight to the final score
   *   <li>Comparing the final probability against the minimum threshold
   * </ol>
   *
   * @param input the input data to evaluate
   * @param ruleGroup the rule group containing conditions and configuration
   * @param engineContextService the context service for state management
   * @param minProbability the minimum probability threshold for boolean conversion
   * @return true if the calculated probability meets or exceeds the threshold, false otherwise
   */
  private boolean evaluateRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService,
      float minProbability) {
    if (ruleGroup.getConditions().isEmpty()) {
      return evaluateEmptyRuleGroup(input, ruleGroup, engineContextService) == 1;
    }

    AtomicInteger totalResult = new AtomicInteger(0);
    AtomicInteger totalWeight = new AtomicInteger(0);

    for (Condition<TInput> condition : ruleGroup.getConditions()) {
      switch (condition) {
        case Rule<TInput, ?> rule -> {
          evaluateRuleGroupRule(input, rule, engineContextService, totalResult, totalWeight);
        }
        case RuleGroup<TInput> nestedGroup -> {
          // Recursively evaluate nested group
          boolean nestedResult =
              evaluateRuleGroup(
                  input, (RuleGroup<TInput>) nestedGroup, engineContextService, minProbability);
          // Retrieve the nested group's actual result and maximumResult from its context
          ConditionContextValue ctx =
              engineContextService
                  .getConditionEvaluationContext()
                  .getConditionContextMap()
                  .get(
                      new ConditionContextKey<>(
                          engineContextService.getInputIdGetter().apply(input),
                          nestedGroup.getId()));
          if (ctx == null || !(ctx instanceof RuleGroupContextValue nestedCtx)) {
            throw new IllegalStateException(
                "Expected RuleGroupContextValue for nested group: " + nestedGroup.getId());
          }
          totalWeight.addAndGet(nestedCtx.getMaximumResult());
          totalResult.addAndGet(nestedCtx.getResult());
        }
      }
    }

    if (totalWeight.get() == 0) {
      totalResult.set(0);
    }

    int finalTotalResult = totalResult.get();
    int finalTotalWeight = totalWeight.get();
    // Apply RuleGroup weight to totalResult and totalWeight
    int groupWeight = ruleGroup.getWeight() != null ? ruleGroup.getWeight() : 1;
    int weightedTotalResult = finalTotalResult * groupWeight;
    int weightedTotalWeight = finalTotalWeight * groupWeight;
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .computeIfAbsent(
            new ConditionContextKey<>(
                engineContextService.getInputIdGetter().apply(input), ruleGroup.getId()),
            _ignored ->
                RuleGroupContextValue.builder()
                    .bias(ruleGroup.getBias())
                    .result(weightedTotalResult)
                    .combinator(ruleGroup.getCombinator())
                    .maximumResult(weightedTotalWeight)
                    .build());

    float score = weightedTotalWeight == 0 ? 0f : (float) weightedTotalResult / weightedTotalWeight;
    return score >= minProbability;
  }

  /**
   * Evaluates a rule within the context of a rule group and accumulates its weighted contribution
   * to the group's total score.
   *
   * <p>This method evaluates the rule and then extracts its result and weight from the evaluation
   * context to update the running totals for the containing rule group.
   *
   * @param <V> the type of value that the rule operates on
   * @param input the input data to evaluate
   * @param rule the rule to evaluate
   * @param engineContextService the context service for accessing evaluation results
   * @param totalResult accumulator for the weighted sum of passing conditions
   * @param totalWeight accumulator for the total possible weight
   */
  private <V> void evaluateRuleGroupRule(
      TInput input,
      Rule<TInput, V> rule,
      EngineContextService<TInput, TInputId> engineContextService,
      AtomicInteger totalResult,
      AtomicInteger totalWeight) {
    evaluateRule(input, rule, engineContextService);
    ConditionContextValue ctx =
        engineContextService
            .getConditionEvaluationContext()
            .getConditionContextMap()
            .get(
                new ConditionContextKey<>(
                    engineContextService.getInputIdGetter().apply(input), rule.getId()));
    if (ctx == null) {
      throw new IllegalStateException("Condition context not found for rule: " + rule.getId());
    }
    switch (ctx) {
      case RuleContextValue<?> ruleContextValue -> {
        int ruleWeight = rule.getWeight();
        totalWeight.addAndGet(ruleWeight);
        totalResult.addAndGet(ctx.getResult() == 1 ? ruleWeight : 0);
      }
      case RuleGroupContextValue ruleGroupContextValue -> {
        throw new IllegalStateException(
            "Expected RuleContextValue but got RuleGroupContextValue for rule: " + rule.getId());
      }
    }
  }

  /**
   * Evaluates an empty rule group based on its bias setting and inversion flag.
   *
   * <p>Empty rule groups have no conditions to evaluate, so their result is determined entirely by
   * their bias configuration. Since there are no conditions to contribute weight, the maximum
   * result is set to 0.
   *
   * @param input the input data (used for context key generation)
   * @param ruleGroup the empty rule group to evaluate
   * @param engineContextService the context service for storing evaluation results
   * @return 1 if the bias evaluation results in true, 0 otherwise
   */
  private Integer evaluateEmptyRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    int result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted() ? 1 : 0;
    return engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .computeIfAbsent(
            conditionContextKey,
            _ignored ->
                RuleGroupContextValue.builder()
                    .bias(ruleGroup.getBias())
                    .result(result)
                    // Empty rule groups inherently cannot have any conditions that contribute to
                    // the result,
                    // so we set the maximum result to 0.
                    .maximumResult(0)
                    .build())
        .getResult();
  }

  @Override
  public void trace(TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    for (Condition<TInput> condition : this.conditions) {
      traceCondition(input, condition, engineContextService);
    }
  }

  private void traceCondition(
      TInput input,
      Condition<TInput> condition,
      EngineContextService<TInput, TInputId> engineContextService) {
    switch (condition) {
      case Rule<TInput, ?> rule -> traceRule(input, rule, engineContextService);
      case RuleGroup<TInput> ruleGroup -> traceRuleGroup(input, ruleGroup, engineContextService);
    }
  }

  private <V> void traceRule(
      TInput input,
      Rule<TInput, V> rule,
      EngineContextService<TInput, TInputId> engineContextService) {
    assert rule.getField() != null;
    assert rule.getOperator() != null;
    assert rule.getValue() != null;
    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V ruleValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, ruleValue);
    int weightedResult = (result ? 1 : 0) * rule.getWeight();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            new ConditionContextKey<>(
                engineContextService.getInputIdGetter().apply(input), rule.getId()),
            RuleContextValue.builder()
                .id(rule.getId())
                .operator(rule.getOperator().getClass().getName())
                .result(weightedResult)
                .maximumResult(rule.getWeight())
                .fieldValue(fieldValue)
                .valueValue(ruleValue)
                .build());
  }

  private void traceRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    if (ruleGroup.getConditions().isEmpty()) {
      traceEmptyRuleGroup(input, ruleGroup, engineContextService);
      return;
    }
    int totalResult = 0;
    int totalWeight = 0;
    for (Condition<TInput> condition : ruleGroup.getConditions()) {
      switch (condition) {
        case Rule<TInput, ?> rule -> {
          traceRule(input, rule, engineContextService);
          ConditionContextValue ctx =
              engineContextService
                  .getConditionEvaluationContext()
                  .getConditionContextMap()
                  .get(
                      new ConditionContextKey<>(
                          engineContextService.getInputIdGetter().apply(input), rule.getId()));
          int ruleWeight = rule.getWeight();
          totalWeight += ruleWeight;
          totalResult += ctx != null ? ctx.getResult() * ruleWeight : 0;
        }
        case RuleGroup<TInput> nestedGroup -> {
          traceRuleGroup(input, nestedGroup, engineContextService);
          ConditionContextValue ctx =
              engineContextService
                  .getConditionEvaluationContext()
                  .getConditionContextMap()
                  .get(
                      new ConditionContextKey<>(
                          engineContextService.getInputIdGetter().apply(input),
                          nestedGroup.getId()));
          if (ctx instanceof RuleGroupContextValue nestedCtx) {
            totalWeight += nestedCtx.getMaximumResult();
            totalResult += nestedCtx.getResult();
          }
        }
      }
    }
    int groupWeight = ruleGroup.getWeight() != null ? ruleGroup.getWeight() : 1;
    int weightedTotalResult = totalResult * groupWeight;
    int weightedTotalWeight = totalWeight * groupWeight;
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            new ConditionContextKey<>(
                engineContextService.getInputIdGetter().apply(input), ruleGroup.getId()),
            RuleGroupContextValue.builder()
                .id(ruleGroup.getId())
                .bias(ruleGroup.getBias())
                .result(weightedTotalResult)
                .combinator(ruleGroup.getCombinator())
                .maximumResult(weightedTotalWeight)
                .build());
  }

  private void traceEmptyRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    int result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted() ? 1 : 0;
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleGroupContextValue.builder()
                .bias(ruleGroup.getBias())
                .result(result)
                .combinator(ruleGroup.getCombinator())
                .maximumResult(0)
                .build());
  }
}
