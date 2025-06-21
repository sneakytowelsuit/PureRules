package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;

import java.util.List;
import java.util.Map;

public interface EvaluationService<TInput> {
    public Map<String, Boolean> evaluate(TInput input);
}
