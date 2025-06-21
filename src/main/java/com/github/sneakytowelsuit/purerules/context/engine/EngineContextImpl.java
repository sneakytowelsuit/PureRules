package com.github.sneakytowelsuit.purerules.context.engine;

import com.github.sneakytowelsuit.purerules.context.condition.DeterministicEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.EvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.ProbabilisticEvaluationContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineContextImpl<TInput> implements EngineContext {
  private final Map<EngineContextKey, EvaluationContext<?>> evaluationContexts = new ConcurrentHashMap<>();

  public EngineContextImpl(TInput input) {
    this.evaluationContexts.put(
            new EngineContextKey(input.hashCode(), EngineMode.DETERMINISTIC),
            new DeterministicEvaluationContext()
    );
    this.evaluationContexts.put(
            new EngineContextKey(input.hashCode(), EngineMode.PROBABILISTIC),
            new ProbabilisticEvaluationContext()
    );
  }

  @Override
  public EvaluationContext<?> getEvaluationContext(EngineContextKey engineContextKey) {
    if (engineContextKey == null) {
      return null;
    }
    return this.evaluationContexts.get(engineContextKey);
  }

  @Override
  public void flushEvaluationContext(EngineContextKey engineContextKey) {
    this.getEvaluationContext(engineContextKey)
        .getConditionResults()
        .clear();
  }
}
