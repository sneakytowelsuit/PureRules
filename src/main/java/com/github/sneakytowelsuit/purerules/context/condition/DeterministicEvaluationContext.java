package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DeterministicEvaluationContext<TInputId> implements ConditionEvaluationContext<TInputId> {

  private final Map<ConditionContextKey<TInputId>, Boolean> conditionResults;

  public DeterministicEvaluationContext() {
    this.conditionResults = new ConcurrentHashMap<>();
  }

  @Override
  public Map<ConditionContextKey<TInputId>, Boolean> getConditionResults() {
    return this.conditionResults;
  }
}
