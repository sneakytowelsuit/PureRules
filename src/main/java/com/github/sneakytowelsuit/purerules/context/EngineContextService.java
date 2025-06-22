package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.context.condition.ConditionEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.DeterministicEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.condition.ProbabilisticEvaluationContext;
import com.github.sneakytowelsuit.purerules.context.field.FieldContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class EngineContextService<TInputId> {
  private ConditionEvaluationContext<TInputId> conditionEvaluationContext;
  private final FieldContext<TInputId> fieldContext;
  private TInputId inputId;

  public EngineContextService(EngineMode engineMode) {
    switch (engineMode) {
      case PROBABILISTIC ->
          this.conditionEvaluationContext = new ProbabilisticEvaluationContext<>();
      case DETERMINISTIC ->
          this.conditionEvaluationContext = new DeterministicEvaluationContext<>();
    }
    this.fieldContext = new FieldContext<>();
  }

  public <TInput> void setInputId(Function<TInput, TInputId> inputIdGetter, TInput input) {
    this.inputId = inputIdGetter.apply(input);
  }

}
