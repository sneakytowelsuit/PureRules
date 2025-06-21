package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public final class ProbabilisticEvaluationContext
    implements EvaluationContext<ProbabilisticConditionEvaluationContextValue> {
  private final Map<ConditionContextKey, ProbabilisticConditionEvaluationContextValue> conditionResults;

  public ProbabilisticEvaluationContext() {
    this.conditionResults = new ConcurrentHashMap<>();
  }
}
