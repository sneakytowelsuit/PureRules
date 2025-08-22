package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.context.condition.RuleContextValue;
import com.github.sneakytowelsuit.purerules.context.condition.RuleGroupContextValue;
import com.github.sneakytowelsuit.purerules.context.field.FieldContextKey;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// spotless:off
/**
 * Implementation of {@link IEvaluationService} that performs deterministic boolean evaluation of
 * rules and conditions.
 *
 * <p>This service evaluates each condition as a strict boolean operation, returning true or false
 * based on exact matches between field values and rule criteria. It handles:
 *
 * <ul>
 *   <li>Individual rules with field comparisons using operators
 *   <li>Rule groups with AND/OR combinators and inversion logic
 *   <li>Nested rule groups with hierarchical evaluation
 *   <li>Empty rule groups evaluated based on bias settings
 * </ul>
 *
 * <p>The evaluation process maintains context information for debugging and analysis, storing
 * intermediate results and field values in the provided {@link EngineContextService}.
 *
 * @param <TInput> the type of input data to be evaluated
 * @param <TInputId> the type used to uniquely identify input instances
 */
// spotless:on
public class DeterministicEvaluationService<TInput, TInputId>
    implements IEvaluationService<TInput, TInputId> {

  // spotless:off
  /** List of conditions to evaluate, defaulting to an empty list. */
  // spotless:on
  private List<Condition<TInput>> conditions = List.of();

  // spotless:off
  /**
   * Creates a new deterministic evaluation service with the specified conditions.
   *
   * <p>The deterministic evaluation service processes conditions using strict boolean logic,
   * where each condition must either completely pass or completely fail. There is no partial
   * matching or confidence scoring.
   *
   * <p><strong>Evaluation Behavior:</strong>
   * <ul>
   *   <li><strong>Rules:</strong> Must exactly match field values using their operators</li>
   *   <li><strong>Rule Groups (AND):</strong> All child conditions must pass</li>
   *   <li><strong>Rule Groups (OR):</strong> At least one child condition must pass</li>
   *   <li><strong>Inverted Groups:</strong> Result is negated after combinator logic</li>
   *   <li><strong>Empty Groups:</strong> Result determined by bias setting</li>
   * </ul>
   *
   * <p><strong>Weight Handling:</strong>
   * In deterministic mode, condition weights are completely ignored. Only the logical
   * structure and boolean results matter.
   *
   * <p><strong>Example:</strong>
   * <pre>{@code
   * List<Condition<Person>> businessRules = Arrays.asList(
   *     Rule.<Person, Integer>builder()
   *         .field(new AgeField())
   *         .operator(new GreaterThanOperator<>())
   *         .value(18)
   *         .build(),
   *     RuleGroup.<Person>builder()
   *         .combinator(Combinator.AND)
   *         .conditions(locationRules)
   *         .build()
   * );
   * 
   * DeterministicEvaluationService<Person, String> service = 
   *     new DeterministicEvaluationService<>(businessRules);
   * }</pre>
   *
   * @param conditions the list of conditions (rules and rule groups) to evaluate
   */
  // spotless:on
  public DeterministicEvaluationService(final List<Condition<TInput>> conditions) {
    this.conditions = conditions;
  }

  // spotless:off
  /**
   * Evaluates all configured conditions against the input using deterministic boolean logic.
   *
   * <p>Each condition is evaluated independently using strict boolean logic and the results are
   * collected into a map. The evaluation process:
   * <ol>
   *   <li>Processes each condition in the configured list</li>
   *   <li>For rules: extracts field value, applies operator, returns boolean result</li>
   *   <li>For rule groups: recursively evaluates child conditions and applies combinator logic</li>
   *   <li>Stores evaluation context for debugging and performance analysis</li>
   *   <li>Returns a map linking condition IDs to their boolean results</li>
   * </ol>
   *
   * <p><strong>Result Interpretation:</strong>
   * <pre>{@code
   * Map<String, Boolean> results = service.evaluate(person, contextService);
   * 
   * // Check individual rule results
   * Boolean ageCheckPassed = results.get("age-rule-id");
   * Boolean locationGroupPassed = results.get("location-group-id");
   * 
   * // All conditions must pass for overall approval
   * boolean overallApproval = results.values().stream()
   *     .allMatch(Boolean::booleanValue);
   * }</pre>
   *
   * <p>The evaluation maintains context information including field values and intermediate
   * results, which can be accessed through the context service for debugging purposes.
   *
   * @param input the input data to evaluate against all configured conditions
   * @param engineContextService the context service for field value caching and state management
   * @return a map where keys are condition IDs and values are their boolean evaluation results
   */
  // spotless:on
  @Override
  public Map<String, Boolean> evaluate(
      TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    return conditions.stream()
        .collect(
            Collectors.toMap(
                Condition::getId,
                condition -> evaluationConditions(input, condition, engineContextService)));
  }

  // spotless:off
  /**
   * Traces the evaluation process for all configured conditions without returning results.
   *
   * <p>This method performs the same evaluation logic as {@link #evaluate} but focuses on
   * populating the evaluation context for debugging and analysis purposes. It's useful for:
   * <ul>
   *   <li>Understanding why certain conditions passed or failed</li>
   *   <li>Analyzing field value extraction and caching behavior</li>
   *   <li>Performance profiling of rule evaluation</li>
   *   <li>Debugging complex rule group logic</li>
   * </ul>
   *
   * <p><strong>Usage Example:</strong>
   * <pre>{@code
   * // Trace evaluation for debugging
   * service.trace(person, contextService);
   * 
   * // Examine context for specific conditions
   * ConditionContext<String> conditionContext = 
   *     contextService.getConditionEvaluationContext();
   *     
   * // Access detailed evaluation information
   * ConditionContextKey<String> key = 
   *     new ConditionContextKey<>(person.getId(), ageRule);
   * RuleContextValue ruleContext = (RuleContextValue) 
   *     conditionContext.getConditionContextMap().get(key);
   *     
   * if (ruleContext != null) {
   *     System.out.println("Field extracted: " + ruleContext.getFieldValue());
   *     System.out.println("Operator result: " + ruleContext.getOperatorResult());
   * }
   * }</pre>
   *
   * @param input the input data to trace evaluation for
   * @param engineContextService the context service for storing trace information
   */
  // spotless:on
  @Override
  public void trace(TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    conditions.forEach(condition -> traceCondition(input, condition, engineContextService));
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
    ConditionContextKey<TInputId> contextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), rule.getId());
    assert rule.getOperator() != null;
    assert rule.getField() != null;
    assert rule.getValue() != null;
    Instant startTime = Instant.now();
    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V valueValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, valueValue);
    Instant endTime = Instant.now();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            contextKey,
            RuleContextValue.builder()
                .id(rule.getId())
                .result(result ? 1 : 0)
                .maximumResult(1)
                .fieldValue(fieldValue)
                .valueValue(valueValue)
                .operator(rule.getOperator().getClass().getName())
                .evaluationDuration(Duration.between(startTime, endTime))
                .build());
  }

  private void traceRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    if (ruleGroup.getConditions().isEmpty()) {
      traceEmptyRuleGroup(input, ruleGroup, engineContextService);
    }
    Instant startTime = Instant.now();
    ruleGroup.getConditions().stream()
        .forEach(
            condition -> {
              switch (condition) {
                case Rule<TInput, ?> rule -> traceRule(input, rule, engineContextService);
                case RuleGroup<TInput> nestedRuleGroup ->
                    traceRuleGroup(input, nestedRuleGroup, engineContextService);
              }
            });
    Instant endTime = Instant.now();
    // After all conditions are traced, update parent RuleGroup context
    ConditionContextKey<TInputId> ruleGroupConditionKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    Integer ruleGroupResult =
        ruleGroup.getConditions().stream()
            .map(
                cond ->
                    engineContextService
                        .getConditionEvaluationContext()
                        .getConditionContextMap()
                        .get(
                            new ConditionContextKey<>(
                                engineContextService.getInputIdGetter().apply(input), cond.getId()))
                        .getResult())
            .reduce(0, Integer::sum);
    Integer ruleGroupMaximumResult =
        ruleGroup.getConditions().stream()
            .map(
                cond ->
                    engineContextService
                        .getConditionEvaluationContext()
                        .getConditionContextMap()
                        .get(
                            new ConditionContextKey<>(
                                engineContextService.getInputIdGetter().apply(input), cond.getId()))
                        .getMaximumResult())
            .reduce(0, Integer::sum);
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            ruleGroupConditionKey,
            RuleGroupContextValue.builder()
                .id(ruleGroup.getId())
                .bias(ruleGroup.getBias())
                .combinator(ruleGroup.getCombinator())
                .result(ruleGroupResult)
                .maximumResult(ruleGroupMaximumResult)
                .evaluationDuration(Duration.between(startTime, endTime))
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
    boolean result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted();
    Instant endTime = Instant.now();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleGroupContextValue.builder()
                .bias(ruleGroup.getBias())
                .maximumResult(0)
                .evaluationDuration(Duration.between(startTime, endTime))
                .id(ruleGroup.getId())
                .result(result ? 1 : 0)
                .combinator(ruleGroup.getCombinator())
                .build());
  }

  // spotless:off
  /**
   * Evaluates a single condition, dispatching to the appropriate evaluation method based on the
   * condition type.
   *
   * @param input the input data to evaluate
   * @param condition the condition to evaluate (either a Rule or RuleGroup)
   * @param engineContextService the context service for state management
   * @return the boolean result of the condition evaluation
   */
  // spotless:on
  private boolean evaluationConditions(
      TInput input,
      Condition<TInput> condition,
      EngineContextService<TInput, TInputId> engineContextService) {
    return switch (condition) {
      case Rule<TInput, ?> rule -> evaluateRule(input, rule, engineContextService);
      case RuleGroup<TInput> ruleGroup -> evaluateRuleGroup(input, ruleGroup, engineContextService);
    };
  }

  // spotless:off
  /**
   * Evaluates a rule group by processing its conditions according to the specified combinator
   * logic.
   *
   * <p>The evaluation handles:
   *
   * <ul>
   *   <li>AND combinators: all conditions must be true
   *   <li>OR combinators: at least one condition must be true
   *   <li>Inversion logic: results can be inverted based on the group's inversion flag
   *   <li>Mixed conditions: both rules and nested rule groups
   * </ul>
   *
   * @param input the input data to evaluate
   * @param ruleGroup the rule group containing conditions and combinator logic
   * @param engineContextService the context service for state management
   * @return the boolean result of the rule group evaluation
   */
  // spotless:on
  private boolean evaluateRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    if (ruleGroup.getConditions().isEmpty()) {
      return evaluateEmptyRuleGroup(input, ruleGroup, engineContextService);
    }
    // Sort the conditions by type
    List<Rule<TInput, ?>> rules = new ArrayList<>();
    List<RuleGroup<TInput>> ruleGroups = new ArrayList<>();
    for (Condition<TInput> condition : ruleGroup.getConditions()) {
      switch (condition) {
        case Rule<TInput, ?> rule -> rules.add(rule);
        case RuleGroup<TInput> ruleGroupCondition -> ruleGroups.add(ruleGroupCondition);
      }
    }
    ConditionContextKey<TInputId> contextKey =
        new ConditionContextKey<TInputId>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    Instant startTime = Instant.now();
    boolean result =
        switch (ruleGroup.getCombinator()) {
              case AND ->
                  rules.stream().allMatch(rule -> evaluateRule(input, rule, engineContextService))
                      && ruleGroups.stream()
                          .allMatch(
                              ruleGroupCondition ->
                                  evaluateRuleGroup(
                                      input, ruleGroupCondition, engineContextService));
              case OR ->
                  rules.stream().anyMatch(rule -> evaluateRule(input, rule, engineContextService))
                      || ruleGroups.stream()
                          .anyMatch(
                              ruleGroupCondition ->
                                  evaluateRuleGroup(
                                      input, ruleGroupCondition, engineContextService));
            }
            ^ ruleGroup.isInverted();
    Instant endTime = Instant.now();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            contextKey,
            RuleGroupContextValue.builder()
                .result(result ? 1 : 0)
                .combinator(ruleGroup.getCombinator())
                .maximumResult(ruleGroup.getConditions().size())
                .bias(ruleGroup.getBias())
                .evaluationDuration(Duration.between(startTime, endTime))
                .build());
    return result;
  }

  // spotless:off
  /**
   * Evaluates an empty rule group based on its bias setting and inversion flag.
   *
   * <p>Empty rule groups are evaluated using their bias configuration, which provides a default
   * result when no conditions are present. The result can then be inverted if the group's inversion
   * flag is set.
   *
   * @param input the input data (used for context key generation)
   * @param ruleGroup the empty rule group to evaluate
   * @param engineContextService the context service for storing evaluation results
   * @return the boolean result based on bias and inversion settings
   */
  // spotless:on
  private boolean evaluateEmptyRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    Instant startTime = Instant.now();
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    boolean result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted();
    Instant endTime = Instant.now();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleGroupContextValue.builder()
                .bias(ruleGroup.getBias())
                .combinator(ruleGroup.getCombinator())
                .id(ruleGroup.getId())
                .result(result ? 1 : 0)
                .evaluationDuration(Duration.between(startTime, endTime))
                .maximumResult(
                    ruleGroup.getWeight()
                        * ruleGroup.getConditions().stream().mapToInt(Condition::getWeight).sum())
                .build());
    return result;
  }

  // spotless:off
  /**
   * Evaluates a single rule by extracting the field value, applying the operator, and comparing
   * against the rule's target value.
   *
   * <p>The evaluation process:
   *
   * <ol>
   *   <li>Extracts the field value from the input using the rule's field extractor
   *   <li>Applies the rule's operator to compare the field value with the target value
   *   <li>Stores the evaluation context including field values and results
   * </ol>
   *
   * @param <V> the type of value being compared
   * @param input the input data to evaluate
   * @param rule the rule containing field, operator, and target value
   * @param engineContextService the context service for caching field values and results
   * @return true if the rule passes, false otherwise
   */
  // spotless:on
  private <V> boolean evaluateRule(
      TInput input,
      Rule<TInput, V> rule,
      EngineContextService<TInput, TInputId> engineContextService) {
    if (rule == null) {
      return false;
    }
    assert rule.getOperator() != null;
    assert rule.getField() != null;
    assert rule.getValue() != null;
    Instant startTime = Instant.now();

    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V valueValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, valueValue);
    Instant endTime = Instant.now();
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), rule.getId());
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleContextValue.builder()
                .id(rule.getId())
                .result(result ? 1 : 0)
                .fieldValue(fieldValue)
                .valueValue(valueValue)
                .operator(rule.getOperator().getClass().getName())
                .evaluationDuration(Duration.between(startTime, endTime))
                .build());
    return result;
  }

  // spotless:off
  /**
   * Extracts and caches the field value from the input for the specified rule.
   *
   * <p>This method uses the context service to cache field values, ensuring that the same field is
   * only extracted once per input instance. The cached value is reused for subsequent rules that
   * use the same field on the same input.
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
    assert rule.getField() != null;
    TInputId inputId = engineContextService.getInputIdGetter().apply(input);
    // Set the field value in the engine context service
    // We can assume the field value is the same across every instance of the same field class for
    // the same input
    return (V)
        engineContextService
            .getFieldContext()
            .getFieldContextMap()
            .computeIfAbsent(
                new FieldContextKey<TInputId>(inputId, rule.getField().getClass().getName()),
                _ignored -> rule.getField().getFieldValueFunction().apply(input));
  }
}
