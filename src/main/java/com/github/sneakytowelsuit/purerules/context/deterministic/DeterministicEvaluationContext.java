package com.github.sneakytowelsuit.purerules.context.deterministic;

import com.github.sneakytowelsuit.purerules.context.EvaluationContext;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class DeterministicEvaluationContext implements EvaluationContext<Boolean> {

    private final Map<List<String>, Boolean> conditionResults;
    public DeterministicEvaluationContext() {
        this.conditionResults = new ConcurrentHashMap<>();
    }
}
