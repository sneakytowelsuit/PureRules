package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public final class DeterministicEvaluationContext implements EvaluationContext<Boolean> {

  private final Map<ConditionContextKey, Boolean> conditionResults;

  public DeterministicEvaluationContext() {
    this.conditionResults = new ConcurrentHashMap<>();
  }
}
