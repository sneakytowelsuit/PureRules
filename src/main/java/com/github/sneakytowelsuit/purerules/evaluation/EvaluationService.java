package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.context.EngineContextService;

import java.util.Map;

public interface EvaluationService<TInput, TInputId> {
  public Map<String, Boolean> evaluate(TInput input, EngineContextService<TInputId> engineContextService);
}
