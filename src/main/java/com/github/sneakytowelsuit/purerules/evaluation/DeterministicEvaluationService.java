package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey;
import com.github.sneakytowelsuit.purerules.context.condition.DeterministicEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.ProbabilisticEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.field.FieldContextKey;
import com.github.sneakytowelsuit.purerules.exceptions.IncompatibleEvaluationContextException;
import com.github.sneakytowelsuit.purerules.utils.ConditionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeterministicEvaluationService<TInput,TInputId> implements EvaluationService<TInput, TInputId> {
  // List of conditions to evaluate, defaulting to an empty list.
  private List<Condition<TInput>> conditions = List.of();

  public DeterministicEvaluationService(final List<Condition<TInput>> conditions) {
    this.conditions = conditions;
  }

  @Override
  public Map<String, Boolean> evaluate(TInput input, EngineContextService<TInputId> engineContextService) {
    return conditions.stream()
        .collect(
            Collectors.toMap(
                Condition::getId, condition -> evaluationConditions(input, condition, engineContextService)));
  }

  private boolean evaluationConditions(TInput input, Condition<TInput> condition, EngineContextService<TInputId> engineContextService) {
    return switch (condition) {
      case Rule<TInput, ?> rule -> evaluateRule(input, rule, engineContextService);
      case RuleGroup<TInput> ruleGroup -> evaluateRuleGroup(input, ruleGroup, engineContextService);
    };
  }

  private boolean evaluateRuleGroup(TInput input, RuleGroup<TInput> ruleGroup, EngineContextService<TInputId> engineContextService) {
    if (ruleGroup.getConditions().isEmpty()) {
      return evaluateEmptyRuleGroup(ruleGroup, engineContextService);
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
    // Evaluate the rules
    return switch (ruleGroup.getCombinator()) {
          case AND ->
              rules.stream().allMatch(rule -> evaluateRule(input, rule, engineContextService))
                  && ruleGroups.stream()
                      .allMatch(ruleGroupCondition -> evaluateRuleGroup(input, ruleGroupCondition, engineContextService));
          case OR ->
              rules.stream().anyMatch(rule -> evaluateRule(input, rule, engineContextService))
                  || ruleGroups.stream()
                      .anyMatch(ruleGroupCondition -> evaluateRuleGroup(input, ruleGroupCondition, engineContextService));
        }
        ^ ruleGroup.isInverted();
  }

  private DeterministicEvaluationContext<TInputId> getEvaluationContext(EngineContextService<TInputId> engineContextService) {
    return switch(engineContextService.getConditionEvaluationContext()) {
      case DeterministicEvaluationContext<TInputId> deterministicEvaluationContext -> deterministicEvaluationContext;
      case ProbabilisticEvaluationContext<TInputId> _ignored -> throw new IncompatibleEvaluationContextException(
          "Expected DeterministicEvaluationContext, but found ProbabilisticEvaluationContext." );
    };
  }

  private boolean evaluateEmptyRuleGroup(RuleGroup<TInput> ruleGroup, EngineContextService<TInputId> engineContextService) {
    return this.getEvaluationContext(engineContextService).getConditionResults()
            .computeIfAbsent(
                    new ConditionContextKey<>(engineContextService.getInputId(), ruleGroup.getId()),
                    _ignored -> ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted()
                    );
  }

  private <V> boolean evaluateRule(TInput input, Rule<TInput, V> rule, EngineContextService<TInputId> engineContextService) {
    if (rule == null) {
      return false;
    }
    assert rule.getOperator() != null;
    assert rule.getField() != null;
    assert rule.getValue() != null;

    V fieldValue = this.getFieldValue(input, rule, engineContextService);
    V valueValue = rule.getValue();
    return this.getEvaluationContext(engineContextService)
            .getConditionResults()
            .computeIfAbsent(new ConditionContextKey<>(engineContextService.getInputId(), rule.getId()),
                    _ignored -> rule.getOperator().test(fieldValue, valueValue));
  }

  private <V> V getFieldValue(TInput input, Rule<TInput, V> rule, EngineContextService<TInputId> engineContextService) {
    assert rule.getField() != null;
    TInputId inputId = engineContextService.getInputId();
    V fieldValue = rule.getField().getFieldValueFunction().apply(input);
    // Set the field value in the engine context service
    // We can assume the field value is the same across every instance of the same field class for the same input
    return (V) engineContextService.getFieldContext().getFieldContextMap()
            .computeIfAbsent(new FieldContextKey<TInputId>(inputId, rule.getField().getClass().getName()), key -> fieldValue);
  }
}
