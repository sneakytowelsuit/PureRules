package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.context.condition.ConditionContext;
import com.github.sneakytowelsuit.purerules.context.field.FieldContext;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class EngineContextService<TInput, TInputId> {
  private final ConditionContext<TInputId> conditionEvaluationContext;
  private final FieldContext<TInputId> fieldContext;
  private final Function<TInput, TInputId> inputIdGetter;

  public EngineContextService(Function<TInput, TInputId> inputIdGetter) {
    this.conditionEvaluationContext = new ConditionContext<>();
    this.fieldContext = new FieldContext<>();
    this.inputIdGetter = inputIdGetter;
  }
}
