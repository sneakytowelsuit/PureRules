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
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// spotless:off
/**
 * Implementation of {@link IEvaluationService} that performs probabilistic evaluation of rules and
 * conditions using weighted scoring and probability thresholds.
 *
 * <p>This service evaluates conditions by calculating probability scores based on weighted
 * contributions from individual rules and rule groups. Unlike deterministic evaluation which
 * uses strict boolean logic, probabilistic evaluation considers the confidence level of matches
 * and allows for fuzzy decision-making based on configurable thresholds.
 *
 * <p><strong>Key Features:</strong>
 * <ul>
 *   <li><strong>Weighted scoring:</strong> Rules and rule groups contribute to the overall score based on their
 *       configured weights</li>
 *   <li><strong>Probability thresholding:</strong> Results are converted to boolean values by comparing calculated
 *       probabilities against a minimum threshold</li>
 *   <li><strong>Recursive evaluation:</strong> Nested rule groups are evaluated recursively with proper weight
 *       propagation</li>
 *   <li><strong>Context preservation:</strong> Maintains detailed evaluation context for debugging and analysis</li>
 * </ul>
 *
 * <p><strong>Evaluation Algorithm:</strong>
 * The probabilistic evaluation follows this process:
 * <ol>
 *   <li>Each individual rule is evaluated as pass/fail</li>
 *   <li>Passing rules contribute their full weight to the score</li>
 *   <li>Failed rules contribute zero to the score</li>
 *   <li>Rule groups are evaluated recursively with their own probability calculations</li>
 *   <li>The final score is normalized against the maximum possible score</li>
 *   <li>The normalized probability is compared against the threshold to determine the final result</li>
 * </ol>
 *
 * <p><strong>Weight Calculation Example:</strong>
 * <pre>{@code
 * // Consider these rules with different weights:
 * Rule<Person, Integer> ageRule = Rule.<Person, Integer>builder()
 *     .weight(3)  // High priority
 *     .field(new AgeField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(18)
 *     .build();
 *     
 * Rule<Person, String> locationRule = Rule.<Person, String>builder()
 *     .weight(1)  // Normal priority
 *     .field(new LocationField())
 *     .operator(new EqualsOperator<>())
 *     .value("US")
 *     .build();
 *
 * // With minimum probability threshold of 0.6:
 * // - If both pass: score = 4/4 = 1.0 (100%) -> TRUE
 * // - If age passes, location fails: score = 3/4 = 0.75 (75%) -> TRUE
 * // - If age fails, location passes: score = 1/4 = 0.25 (25%) -> FALSE
 * // - If both fail: score = 0/4 = 0.0 (0%) -> FALSE
 * }</pre>
 *
 * <p><strong>Rule Group Probability:</strong>
 * Rule groups are evaluated by first calculating their internal probability and then
 * contributing their weight only if they meet the threshold:
 * <pre>{@code
 * RuleGroup<Person> preferences = RuleGroup.<Person>builder()
 *     .combinator(Combinator.OR)
 *     .weight(2)
 *     .conditions(Arrays.asList(
 *         preferenceRule1,  // weight=1
 *         preferenceRule2   // weight=1
 *     ))
 *     .build();
 *
 * // If preferenceRule1 passes and preferenceRule2 fails:
 * // - Internal group probability: 1/2 = 0.5
 * // - If group threshold >= 0.5: contributes full weight (2) to parent
 * // - If group threshold > 0.5: contributes zero to parent
 * }</pre>
 *
 * <p><strong>Use Cases:</strong>
 * Probabilistic evaluation is particularly useful for:
 * <ul>
 *   <li>Recommendation systems where partial matches are valuable</li>
 *   <li>Risk assessment with varying confidence levels</li>
 *   <li>Eligibility systems with weighted criteria</li>
 *   <li>Scoring systems where multiple factors contribute to a decision</li>
 * </ul>
 *
 * <p><strong>Configuration Example:</strong>
 * <pre>{@code
 * // Create engine with 70% confidence threshold
 * PureRulesEngine<Person, String> engine = PureRulesEngine
 *     .getProbabilisticEngine(Person::getId, 0.7f, conditions);
 *
 * // Only results with >= 70% confidence will return true
 * Map<String, Boolean> results = engine.evaluate(person);
 * }</pre>
 *
 * @param <TInput> the type of input data to be evaluated
 * @param <TInputId> the type used to uniquely identify input instances
 * 
 * @see DeterministicEvaluationService
 * @see IEvaluationService
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine#getProbabilisticEngine
 */
// spotless:on
public class ProbabilisticEvaluationService<TInput, TInputId>
    implements IEvaluationService<TInput, TInputId> {
  // spotless:off
  /**
   * The minimum probability threshold for considering a condition as passing. This is applied at
   * the root of each condition in the list of conditions provided
   */
  // spotless:on
  private final Float minimumProbability;

  // spotless:off
  /** The list of conditions to evaluate against input data. */
  // spotless:on
  private final List<Condition<TInput>> conditions;

  // spotless:off
  /**
   * Creates a new probabilistic evaluation service with the specified conditions and minimum
   * probability threshold.
   *
   * <p>The minimum probability threshold determines the confidence level required for conditions
   * to be considered as passing. This threshold applies to the normalized probability score
   * calculated from weighted rule contributions.
   *
   * <p><strong>Threshold Guidelines:</strong>
   * <ul>
   *   <li><strong>0.0 - 0.3:</strong> Very lenient, most conditions will pass</li>
   *   <li><strong>0.4 - 0.6:</strong> Moderate confidence required</li>
   *   <li><strong>0.7 - 0.9:</strong> High confidence required, strict evaluation</li>
   *   <li><strong>0.9 - 1.0:</strong> Nearly perfect match required</li>
   * </ul>
   *
   * <p><strong>Example:</strong>
   * <pre>{@code
   * List<Condition<Person>> eligibilityRules = Arrays.asList(
   *     ageRule,           // weight=3
   *     locationRule,      // weight=2  
   *     preferenceGroup    // weight=1
   * );
   * 
   * // Require 75% confidence for eligibility
   * ProbabilisticEvaluationService<Person, String> service = 
   *     new ProbabilisticEvaluationService<>(eligibilityRules, 0.75f);
   * }</pre>
   *
   * @param conditions the list of conditions (rules and rule groups) to evaluate
   * @param minimumProbability the minimum probability threshold (0.0 to 1.0) required for a
   *     condition to be considered as passing. Values outside this range may produce
   *     unexpected results.
   */
  // spotless:on
  public ProbabilisticEvaluationService(
      List<Condition<TInput>> conditions, Float minimumProbability) {
    this.conditions = conditions;
    this.minimumProbability = minimumProbability;
  }

  // spotless:off
  /**
   * Validates and returns the minimum probability threshold.
   *
   * @return the validated minimum probability threshold
   * @throws IllegalArgumentException if the minimum probability is null or outside the range [0, 1]
   */
  // spotless:on
  private Float getMinimumProbability() {
    if (this.minimumProbability == null
        || this.minimumProbability < 0f
        || this.minimumProbability > 1f) {
      throw new IllegalArgumentException("Minimum probability must be between 0 and 1");
    }
    return this.minimumProbability;
  }

  // spotless:off
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
  // spotless:on
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

  // spotless:off
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
  // spotless:on
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

  // spotless:off
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
  // spotless:on
  private <V> boolean evaluateRule(
      TInput input,
      Rule<TInput, V> rule,
      EngineContextService<TInput, TInputId> engineContextService) {
    Instant startTime = Instant.now();
    if (rule == null) {
      Instant endTime = Instant.now();
      // Default to false if the rule is null
      engineContextService
          .getConditionEvaluationContext()
          .getConditionContextMap()
          .put(
              new ConditionContextKey<>(
                  engineContextService.getInputIdGetter().apply(input), rule.getId()),
              RuleContextValue.builder()
                  .id(rule.getId())
                  .result(0)
                  .evaluationDuration(Duration.between(startTime, endTime))
                  .maximumResult(rule.getWeight())
                  .build());
      return false;
    }
    assert rule.getField() != null;
    assert rule.getOperator() != null;
    assert rule.getValue() != null;

    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V ruleValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, ruleValue);
    Instant endTime = Instant.now();
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
                .evaluationDuration(Duration.between(startTime, endTime))
                // Rules are weighed based solely on their weight since they are leaf nodes
                .maximumResult(rule.getWeight())
                .build());
    return result;
  }

  // spotless:off
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
  // spotless:on
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

  // spotless:off
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
  // spotless:on
  private boolean evaluateRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService,
      float minProbability) {
    if (ruleGroup.getConditions().isEmpty()) {
      return evaluateEmptyRuleGroup(input, ruleGroup, engineContextService) == 1;
    }
    Instant startTime = Instant.now();
    AtomicInteger totalResult = new AtomicInteger(0);
    AtomicInteger totalWeight = new AtomicInteger(0);

    for (Condition<TInput> condition : ruleGroup.getConditions()) {
      switch (condition) {
        case Rule<TInput, ?> rule -> {
          boolean ruleResult = evaluateRule(input, rule, engineContextService);
          int ruleWeight = rule.getWeight();
          totalWeight.addAndGet(ruleWeight);
          if (ruleResult) {
            totalResult.addAndGet(ruleWeight);
          }
        }
        case RuleGroup<TInput> nestedGroup -> {
          // Recursively evaluate nested group
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
    Instant endTime = Instant.now();

    int weightedTotalResult = totalResult.get() * ruleGroup.getWeight();
    int weightedTotalWeight = totalWeight.get() * ruleGroup.getWeight();
    float score = (float) weightedTotalResult / (float) weightedTotalWeight;
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
                    .evaluationDuration(Duration.between(startTime, endTime))
                    .combinator(ruleGroup.getCombinator())
                    .maximumResult(weightedTotalWeight)
                    .build());
    return score >= minProbability;
  }

  // spotless:off
  /**
   * Evaluates a rule within the context of a rule group and accumulates its weighted contribution
   * to the group's total score.
   *
   * <p>This method evaluates the rule and then extracts its result and weight from the evaluation
   * context to update the running totals for the containing rule group.
   *
   * @param <V> the type of value that the rule operates on
   * @param input the input data to evaluate
   * @param rule the rule to evaluate /** Evaluates an empty rule group based on its bias setting
   *     and inversion flag.
   *     <p>Empty rule groups have no conditions to evaluate, so their result is determined entirely
   *     by their bias configuration. Since there are no conditions to contribute weight, the
   *     maximum result is set to 0.
   * @param input the input data (used for context key generation)
   * @param ruleGroup the empty rule group to evaluate
   * @param engineContextService the context service for storing evaluation results
   * @return 1 if the bias evaluation results in true, 0 otherwise
   */
  // spotless:on
  private Integer evaluateEmptyRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    Instant startTime = Instant.now();
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    int result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted() ? 1 : 0;
    Instant endTime = Instant.now();
    return engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .computeIfAbsent(
            conditionContextKey,
            _ignored ->
                RuleGroupContextValue.builder()
                    .bias(ruleGroup.getBias())
                    .evaluationDuration(Duration.between(startTime, endTime))
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
    Instant startTime = Instant.now();
    assert rule.getField() != null;
    assert rule.getOperator() != null;
    assert rule.getValue() != null;
    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V ruleValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, ruleValue);
    Instant endTime = Instant.now();
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
                .evaluationDuration(Duration.between(startTime, endTime))
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
    Instant startTime = Instant.now();
    for (Condition<TInput> condition : ruleGroup.getConditions()) {
      switch (condition) {
        case Rule<TInput, ?> rule -> {
          traceRule(input, rule, engineContextService);
        }
        case RuleGroup<TInput> nestedGroup -> {
          traceRuleGroup(input, nestedGroup, engineContextService);
        }
      }
      ConditionContextValue ctx =
          engineContextService
              .getConditionEvaluationContext()
              .getConditionContextMap()
              .get(
                  new ConditionContextKey<>(
                      engineContextService.getInputIdGetter().apply(input), condition.getId()));
      if (ctx != null) {
        totalResult += ctx.getResult();
        totalWeight += ctx.getMaximumResult();
      }
    }
    Instant endTime = Instant.now();
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
                .evaluationDuration(Duration.between(startTime, endTime))
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
    Instant startTime = Instant.now();
    int result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted() ? 1 : 0;
    Instant endTime = Instant.now();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleGroupContextValue.builder()
                .bias(ruleGroup.getBias())
                .evaluationDuration(Duration.between(startTime, endTime))
                .result(result)
                .combinator(ruleGroup.getCombinator())
                .maximumResult(0)
                .build());
  }
}
