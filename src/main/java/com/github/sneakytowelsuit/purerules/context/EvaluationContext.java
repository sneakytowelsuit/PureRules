package com.github.sneakytowelsuit.purerules.context;

public sealed interface EvaluationContext<T> permits DeterministicEvaluationContext, ProbabilisticEvaluationContext {
}
