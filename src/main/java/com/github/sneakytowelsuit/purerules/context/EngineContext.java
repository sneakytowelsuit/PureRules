package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.engine.EngineMode;

public interface EngineContext {
    <T> EvaluationContext<T> getEvaluationContext(EngineMode engineMode);
    void flushEvaluationContext(EngineMode engineMode);
}
