package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.context.condition.RuleContextValue;
import com.github.sneakytowelsuit.purerules.context.condition.RuleGroupContextValue;
import com.github.sneakytowelsuit.purerules.context.field.FieldContextKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EvaluationService} that performs deterministic boolean evaluation of
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
public class DeterministicEvaluationService<TInput, TInputId>
    implements EvaluationService<TInput, TInputId> {

  /** List of conditions to evaluate, defaulting to an empty list. */
  private List<Condition<TInput>> conditions = List.of();

  /**
   * Creates a new deterministic evaluation service with the specified conditions.
   *
   * @param conditions the list of conditions (rules and rule groups) to evaluate
   */
  public DeterministicEvaluationService(final List<Condition<TInput>> conditions) {
    this.conditions = conditions;
  }

  /**
   * Evaluates all configured conditions against the input using deterministic boolean logic.
   *
   * <p>Each condition is evaluated independently and the results are collected into a map. The
   * evaluation maintains context information including field values and intermediate results.
   *
   * @param input the input data to evaluate
   * @param engineContextService the context service for caching and state management
   * @return a map of condition IDs to their boolean evaluation results
   */
  @Override
  public Map<String, Boolean> evaluate(
      TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    return conditions.stream()
        .collect(
            Collectors.toMap(
                Condition::getId,
                condition -> evaluationConditions(input, condition, engineContextService)));
  }

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

    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V valueValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, valueValue);
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), rule.getId());
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleContextValue.builder()
                .ruleId(rule.getId())
                .result(result ? 1 : 0)
                .maximumResult(1)
                .fieldValue(fieldValue)
                .valueValue(valueValue)
                .operator(rule.getOperator().getClass().getName())
                .build());
  }

  private void traceRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    if (ruleGroup.getConditions().isEmpty()) {
      traceEmptyRuleGroup(input, ruleGroup, engineContextService);
    }

    ruleGroup.getConditions().stream()
        .sorted(
            (c1, c2) -> {
              if (c1 instanceof Rule && c2 instanceof Rule) {
                return 0; // Both are rules, no specific order
              } else if (c1 instanceof RuleGroup && c2 instanceof RuleGroup) {
                return 0; // Both are rule groups, no specific order
              } else if (c1 instanceof Rule) {
                return -1; // Rule comes before RuleGroup
              } else {
                return 1; // RuleGroup comes after Rule
              }
            })
        .forEach(
            condition -> {
              switch (condition) {
                case Rule<TInput, ?> rule -> traceRule(input, rule, engineContextService);
                case RuleGroup<TInput> nestedRuleGroup ->
                    traceRuleGroup(input, nestedRuleGroup, engineContextService);
              }
            });
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
                .bias(ruleGroup.getBias())
                .combinator(ruleGroup.getCombinator())
                .result(ruleGroupResult)
                .maximumResult(ruleGroupMaximumResult)
                .build());
  }

  private void traceEmptyRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    boolean result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleGroupContextValue.builder()
                .bias(ruleGroup.getBias())
                .result(result ? 1 : 0)
                .combinator(ruleGroup.getCombinator())
                .build());
  }

  /**
   * Evaluates a single condition, dispatching to the appropriate evaluation method based on the
   * condition type.
   *
   * @param input the input data to evaluate
   * @param condition the condition to evaluate (either a Rule or RuleGroup)
   * @param engineContextService the context service for state management
   * @return the boolean result of the condition evaluation
   */
  private boolean evaluationConditions(
      TInput input,
      Condition<TInput> condition,
      EngineContextService<TInput, TInputId> engineContextService) {
    return switch (condition) {
      case Rule<TInput, ?> rule -> evaluateRule(input, rule, engineContextService);
      case RuleGroup<TInput> ruleGroup -> evaluateRuleGroup(input, ruleGroup, engineContextService);
    };
  }

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
                .build());
    return result;
  }

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
  private boolean evaluateEmptyRuleGroup(
      TInput input,
      RuleGroup<TInput> ruleGroup,
      EngineContextService<TInput, TInputId> engineContextService) {
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), ruleGroup.getId());
    boolean result = ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleGroupContextValue.builder()
                .bias(ruleGroup.getBias())
                .result(result ? 1 : 0)
                .build());
    return result;
  }

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

    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V valueValue = rule.getValue();
    boolean result = rule.getOperator().test(fieldValue, valueValue);
    ConditionContextKey<TInputId> conditionContextKey =
        new ConditionContextKey<>(
            engineContextService.getInputIdGetter().apply(input), rule.getId());
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .put(
            conditionContextKey,
            RuleContextValue.builder()
                .ruleId(rule.getId())
                .result(result ? 1 : 0)
                .fieldValue(fieldValue)
                .valueValue(valueValue)
                .operator(rule.getOperator().getClass().getName())
                .build());
    return result;
  }

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
