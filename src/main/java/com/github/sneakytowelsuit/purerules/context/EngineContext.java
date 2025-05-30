package com.github.sneakytowelsuit.purerules.context;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineContext {
    @Getter
    public static class DeterministicEvaluationContext {
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
    public void instantiateEvaluationContext(Long threadId) {
       this.getThreadIdToContextMap().putIfAbsent(threadId, new DeterministicEvaluationContext());
    }
    private Map<Long, DeterministicEvaluationContext> getThreadIdToContextMap() {
        return this.threadIdToContextMap;
    }
    public DeterministicEvaluationContext getEvaluationContext(Long threadId) {
        return this.getThreadIdToContextMap().computeIfAbsent(threadId, k -> new DeterministicEvaluationContext());
    }
    public void clearContext(Long threadId) {
        this.getThreadIdToContextMap().remove(threadId);
    }
}
