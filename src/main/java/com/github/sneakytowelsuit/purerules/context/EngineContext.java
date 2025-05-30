package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.conditions.Field;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineContext {
    @Getter
    public static class EvaluationContext {
        private final Map<List<String>, Boolean> conditionResults;
        public EvaluationContext() {
            this.conditionResults = new ConcurrentHashMap<>();
        }
    }

    // TODO: Consider adding cache for field getter return values, agnostic of thread
    private final Map<Long, EvaluationContext> threadIdToContextMap;
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
       this.getThreadIdToContextMap().putIfAbsent(threadId, new EvaluationContext());
    }
    private Map<Long, EvaluationContext> getThreadIdToContextMap() {
        return this.threadIdToContextMap;
    }
    public EvaluationContext getEvaluationContext(Long threadId) {
        return this.getThreadIdToContextMap().computeIfAbsent(threadId, k -> new EvaluationContext());
    }
    public void clearContext(Long threadId) {
        this.getThreadIdToContextMap().remove(threadId);
    }
}
