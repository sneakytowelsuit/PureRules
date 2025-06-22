package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;

public sealed interface ConditionEvaluationContext<TInputId>
    permits DeterministicEvaluationContext, ProbabilisticEvaluationContext {
    public Map<ConditionContextKey<TInputId>, ?> getConditionResults();
}
