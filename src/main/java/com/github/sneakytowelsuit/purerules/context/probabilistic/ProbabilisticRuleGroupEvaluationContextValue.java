package com.github.sneakytowelsuit.purerules.context.probabilistic;

import lombok.Builder;

@Builder
public final class ProbabilisticRuleGroupEvaluationContextValue
    implements ProbabilisticConditionEvaluationContextValue {
  private final Integer max;
  private final Integer min;
  private final Integer resultCount;
}
