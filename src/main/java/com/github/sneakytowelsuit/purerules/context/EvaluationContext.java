package com.github.sneakytowelsuit.purerules.context;

import java.util.Map;

public sealed interface EvaluationContext<T>
    permits DeterministicEvaluationContext, ProbabilisticEvaluationContext {

  public Map<ContextKey, T> getConditionResults();
}
