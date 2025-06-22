package com.github.sneakytowelsuit.purerules.context.condition;

import lombok.Builder;

@Builder
public final class ProbabilisticRuleGroupEvaluationContextValue
    implements ProbabilisticConditionEvaluationContextValue {
  private final Integer maxCount;
  private final Integer resultCount;
}
