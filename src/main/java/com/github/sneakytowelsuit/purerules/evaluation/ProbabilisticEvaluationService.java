package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;

import java.util.List;
import java.util.Map;

public class ProbabilisticEvaluationService<TInput> implements EvaluationService<TInput>{
    private final Float minimumProbability;
    private final List<Condition<TInput>> conditions;
    public ProbabilisticEvaluationService(List<Condition<TInput>> conditions, Float minimumProbability) {
        this.conditions = conditions;
        this.minimumProbability = minimumProbability;
    }
    @Override
    public Map<String, Boolean> evaluate(TInput tInput) {
        return Map.of();
    }
}
