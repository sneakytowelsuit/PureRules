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

public class DeterministicEvaluationService<TInput, TInputId>
    implements EvaluationService<TInput, TInputId> {
  // List of conditions to evaluate, defaulting to an empty list.
  private List<Condition<TInput>> conditions = List.of();

  public DeterministicEvaluationService(final List<Condition<TInput>> conditions) {
    this.conditions = conditions;
  }

  @Override
  public Map<String, Boolean> evaluate(
      TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    return conditions.stream()
        .collect(
            Collectors.toMap(
                Condition::getId,
                condition -> evaluationConditions(input, condition, engineContextService)));
  }

  private boolean evaluationConditions(
      TInput input,
      Condition<TInput> condition,
      EngineContextService<TInput, TInputId> engineContextService) {
    return switch (condition) {
      case Rule<TInput, ?> rule -> evaluateRule(input, rule, engineContextService);
      case RuleGroup<TInput> ruleGroup -> evaluateRuleGroup(input, ruleGroup, engineContextService);
    };
  }

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
                // Deterministic evaluation doesn't need to account for the weight of the conditions
                .maximumResult(ruleGroup.getConditions().size())
                .combinator(ruleGroup.getCombinator())
                .build());
    return result;
  }

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
