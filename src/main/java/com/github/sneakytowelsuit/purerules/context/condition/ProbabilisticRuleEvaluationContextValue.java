package com.github.sneakytowelsuit.purerules.context.condition;

import lombok.Builder;

@Builder
public final class ProbabilisticRuleEvaluationContextValue<TValue>
    implements ProbabilisticConditionEvaluationContextValue {
  private final TValue fieldValue;
  private final TValue ruleValue;
  private final String operatorName;
  private final Boolean result;
}
