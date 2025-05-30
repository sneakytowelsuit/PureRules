package com.github.sneakytowelsuit.purerules.context.probabilistic;

import com.github.sneakytowelsuit.purerules.conditions.Operator;
import lombok.Builder;

@Builder
public final class ProbabilisticRuleEvaluationContextValue<V> implements ProbabilisticConditionEvaluationContextValue {
   private final V fieldValue;
   private final V ruleValue;
   private final String operatorName;
   private final Boolean result;
}
