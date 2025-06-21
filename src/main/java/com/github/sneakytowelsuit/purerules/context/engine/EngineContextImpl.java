package com.github.sneakytowelsuit.purerules.context.engine;

import com.github.sneakytowelsuit.purerules.context.condition.DeterministicEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.EvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.ProbabilisticEvaluationContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import java.util.EnumMap;
import lombok.Getter;

@Getter
public class EngineContextImpl implements EngineContext {
  private final EnumMap<EngineMode, EvaluationContext<?>> evaluationContexts =
      new EnumMap<>(EngineMode.class);

  public EngineContextImpl() {
    this.evaluationContexts.put(EngineMode.DETERMINISTIC, new DeterministicEvaluationContext());
    this.evaluationContexts.put(EngineMode.PROBABILISTIC, new ProbabilisticEvaluationContext());
  }

  @Override
  public EvaluationContext<?> getEvaluationContext(EngineMode engineMode) {
    return this.getEvaluationContexts().get(engineMode);
  }

  @Override
  public void flushEvaluationContext(EngineMode engineMode) {
    EvaluationContext<?> context = this.getEvaluationContexts().get(engineMode);
    if (context != null) {
      context.getConditionResults().clear();
    }
  }
}
