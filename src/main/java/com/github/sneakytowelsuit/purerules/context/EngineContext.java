package com.github.sneakytowelsuit.purerules.context;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineContext {
    @Getter
    public final static class DeterministicEvaluationContext {
        private final Map<List<String>, Boolean> conditionResults;
        public DeterministicEvaluationContext() {
            this.conditionResults = new ConcurrentHashMap<>();
        }
    }

    private final Map<Long, DeterministicEvaluationContext> threadIdToContextMap;
    private static EngineContext instance;
    public static EngineContext getInstance() {
        if (instance == null) {
            instance = new EngineContext();
        }
        return instance;
    }
    private EngineContext() {
        this.threadIdToContextMap = new ConcurrentHashMap<>();
    }
    public void instantiateDeterministicEvaluationContext(Long threadId) {
       this.getThreadIdBoundDeterministicEvaluationContext().putIfAbsent(threadId, new DeterministicEvaluationContext());
    }
    private Map<Long, DeterministicEvaluationContext> getThreadIdBoundDeterministicEvaluationContext() {
        return this.threadIdToContextMap;
    }
    public DeterministicEvaluationContext getDeterministicEvaluationContext(Long threadId) {
        return this.getThreadIdBoundDeterministicEvaluationContext().computeIfAbsent(threadId, k -> new DeterministicEvaluationContext());
    }
    public void clearContext(Long threadId) {
        this.getThreadIdBoundDeterministicEvaluationContext().remove(threadId);
    }
}
