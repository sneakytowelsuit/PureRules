package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

public final class ProbabilisticEvaluationContext<TInputId> implements ConditionEvaluationContext<TInputId> {
  private final Map<ConditionContextKey<TInputId>, ProbabilisticConditionEvaluationContextValue> conditionResults;

  public ProbabilisticEvaluationContext() {
    this.conditionResults = new ConcurrentHashMap<>();
  }

  @Override
  public Map<ConditionContextKey<TInputId>, ?> getConditionResults() {
    return this.conditionResults;
  }
}
