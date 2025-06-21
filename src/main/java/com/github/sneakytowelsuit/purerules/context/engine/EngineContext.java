package com.github.sneakytowelsuit.purerules.context.engine;

import com.github.sneakytowelsuit.purerules.context.condition.EvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineContext<I> {
  private final Map<EngineContextKey<I>, EvaluationContext<?>> evaluationContexts = new ConcurrentHashMap<>();

  public EngineContext() {}

  public <T> EvaluationContext<?> getEvaluationContext(EngineContextKey<T> engineContextKey) {
    return  evaluationContexts.get(engineContextKey);
  }

  public <T> void flushEvaluationContext(EngineContextKey<T> engineContextKey) {
    evaluationContexts.remove(engineContextKey);
  }
}
