package com.github.sneakytowelsuit.purerules.context.engine;

import com.github.sneakytowelsuit.purerules.context.condition.EvaluationContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;

public interface EngineContext {
  <T> EvaluationContext<T> getEvaluationContext(EngineMode engineMode);

  void flushEvaluationContext(EngineMode engineMode);
}
