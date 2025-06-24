package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContext;
import com.github.sneakytowelsuit.purerules.context.field.FieldContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
public class EngineContextService<TInput, TInputId> {
  private final ConditionContext<TInputId> conditionEvaluationContext;
  private final FieldContext<TInputId> fieldContext;
  private TInputId inputId;

  public EngineContextService(EngineMode engineMode, List<Condition<TInput>> conditions) {
    this.conditionEvaluationContext = new ConditionContext<>();
    this.fieldContext = new FieldContext<>();
  }

  public <TInput> void setInputId(Function<TInput, TInputId> inputIdGetter, TInput input) {
    this.inputId = inputIdGetter.apply(input);
  }

}
