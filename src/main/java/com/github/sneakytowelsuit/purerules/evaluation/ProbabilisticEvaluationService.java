package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;

import java.util.List;
import java.util.Map;

public class ProbabilisticEvaluationService<TInput, TInputId> implements EvaluationService<TInput, TInputId> {
  private final Float minimumProbability;
  private final List<Condition<TInput>> conditions;

  public ProbabilisticEvaluationService(
      List<Condition<TInput>> conditions, Float minimumProbability) {
    this.conditions = conditions;
    this.minimumProbability = minimumProbability;
  }

  @Override
  public Map<String, Boolean> evaluate(TInput input, EngineContextService<TInput, TInputId> engineContextService) {
    return Map.of();
  }
}
