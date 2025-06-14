package com.github.sneakytowelsuit.purerules.context;

import lombok.Builder;

@Builder
public final class ProbabilisticRuleGroupEvaluationContextValue
    implements ProbabilisticConditionEvaluationContextValue {
  private final Integer max;
  private final Integer resultCount;
}
