package com.github.sneakytowelsuit.purerules.context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public final class ProbabilisticEvaluationContext
    implements EvaluationContext<ProbabilisticConditionEvaluationContextValue> {
  private final Map<ContextKey, ProbabilisticConditionEvaluationContextValue> conditionResults;

  public ProbabilisticEvaluationContext() {
    this.conditionResults = new ConcurrentHashMap<>();
  }
}
