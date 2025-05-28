package com.github.sneakytowelsuit.purerules;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EvaluationContextCache {
    @Getter
    public static class EvaluationContext {
        private final Map<String, Boolean> conditionResults;
        public EvaluationContext() {
            this.conditionResults = new ConcurrentHashMap<>();
        }
    }

    private final Map<Long, EvaluationContext> threadIdToContextMap;
    private static EvaluationContextCache instance;
    public static EvaluationContextCache getInstance() {
        if (instance == null) {
            instance = new EvaluationContextCache();
        }
        return instance;
    }
    private EvaluationContextCache() {
        this.threadIdToContextMap = new ConcurrentHashMap<>();
    }
    public void instantiateContext() {
       this.getThreadIdToContextMap().put(Thread.currentThread().threadId(), new EvaluationContext());
    }
    private Map<Long, EvaluationContext> getThreadIdToContextMap() {
        return this.threadIdToContextMap;
    }
    public EvaluationContext getEvaluationContext() {
        return this.getThreadIdToContextMap().get(Thread.currentThread().threadId());
    }
    public void clearContext() {
        this.getThreadIdToContextMap().remove(Thread.currentThread().threadId());
    }
}
