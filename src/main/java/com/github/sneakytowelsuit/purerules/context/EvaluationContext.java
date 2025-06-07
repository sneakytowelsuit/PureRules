package com.github.sneakytowelsuit.purerules.context;

import java.util.List;
import java.util.Map;

public sealed interface EvaluationContext<T>
    permits DeterministicEvaluationContext, ProbabilisticEvaluationContext {

  public Map<List<String>, T> getConditionResults();
}
