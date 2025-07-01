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

public class ProbabilisticEvaluationService<TInput, TInputId>
    implements EvaluationService<TInput, TInputId> {
  private final Float minimumProbability;
  private final List<Condition<TInput>> conditions;

  public ProbabilisticEvaluationService(
      List<Condition<TInput>> conditions, Float minimumProbability) {
    this.conditions = conditions;
    this.minimumProbability = minimumProbability;
  }

  private Float getMinimumProbability() {
    if (this.minimumProbability == null
        || this.minimumProbability < 0f
        || this.minimumProbability > 1f) {
      throw new IllegalArgumentException("Minimum probability must be between 0 and 1");
    }
    return this.minimumProbability;
  }

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

  // Updated to accept minProbability
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
   * Evaluates a single rule against the input. In cases where the list of conditions contains a
   * single rule, this method will be called and will simply return the result of the rule
   * evaluation. In cases where the list of conditions contains a rule group, this method will be
   * called for each rule in the group recursively.
   *
   * @param input The input to evaluate against the rule.
   * @param rule The rule to evaluate.
   * @param engineContextService The context service that provides access to the field and condition
   *     evaluation contexts.
   * @return true if the rule evaluates to true, false otherwise.
   * @param <V> The type of the value that the rule operates on. This is typically a field value
   *     from the input.
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
                .ruleId(rule.getId())
                .operator(rule.getOperator().getClass().getName())
                .result(result ? 1 : 0)
                // Rules are weighed based solely on their weight since they are leaf nodes
                .maximumResult(rule.getWeight())
                .build());
    return result;
  }

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

  // Updated to accept minProbability
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
          evaluateNestedRuleGroup(input, ruleGroup, engineContextService, totalResult, totalWeight, minProbability);
        }
      }
    }

    if (totalWeight.get() == 0) {
      totalResult.set(0);
    }

    int finalTotalResult = totalResult.get();
    int finalTotalWeight = totalWeight.get();
    engineContextService
        .getConditionEvaluationContext()
        .getConditionContextMap()
        .computeIfAbsent(
            new ConditionContextKey<>(
                engineContextService.getInputIdGetter().apply(input), ruleGroup.getId()),
            _ignored ->
                RuleGroupContextValue.builder()
                    .bias(ruleGroup.getBias())
                    .result(finalTotalResult)
                    .combinator(ruleGroup.getCombinator())
                    .maximumResult(finalTotalWeight)
                    .build());

    float score = finalTotalWeight == 0 ? 0f : (float) finalTotalResult / finalTotalWeight;
    return score >= minProbability;
  }

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

  private void evaluateNestedRuleGroup(
          TInput input,
            RuleGroup<TInput> nestedGroup,
            EngineContextService<TInput, TInputId> engineContextService,
            AtomicInteger totalResult,
            AtomicInteger totalWeight,
            float minProbability
  ) {
    // Recursively evaluate nested group
    evaluateRuleGroup(input, (RuleGroup<TInput>) nestedGroup, engineContextService, minProbability);
    // Retrieve the nested group's actual result and maximumResult from its context
    ConditionContextValue ctx =
            engineContextService
                    .getConditionEvaluationContext()
                    .getConditionContextMap()
                    .get(
                            new ConditionContextKey<>(
                                    engineContextService.getInputIdGetter().apply(input), nestedGroup.getId()));
    if (ctx == null || !(ctx instanceof RuleGroupContextValue nestedCtx)) {
      throw new IllegalStateException(
              "Expected RuleGroupContextValue for nested group: " + nestedGroup.getId());
    }
    totalWeight.addAndGet(nestedCtx.getMaximumResult());
    totalResult.addAndGet(nestedCtx.getResult());
  }

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
}
