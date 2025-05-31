package com.github.sneakytowelsuit.purerules.context.probabilistic;

import com.github.sneakytowelsuit.purerules.context.EvaluationContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public class ProbabilisticEvaluationContext
    implements EvaluationContext<ProbabilisticConditionEvaluationContextValue> {
  private final Map<List<String>, ProbabilisticConditionEvaluationContextValue> conditionResults;

  public ProbabilisticEvaluationContext() {
    this.conditionResults = new ConcurrentHashMap<>();
  }
}
