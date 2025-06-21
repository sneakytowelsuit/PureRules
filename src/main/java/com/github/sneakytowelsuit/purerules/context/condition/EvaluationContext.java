package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;

public sealed interface EvaluationContext<T>
    permits DeterministicEvaluationContext, ProbabilisticEvaluationContext {

  public Map<ConditionContextKey, T> getConditionResults();
}
