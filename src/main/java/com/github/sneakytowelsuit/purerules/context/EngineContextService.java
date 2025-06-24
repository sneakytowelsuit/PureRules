package com.github.sneakytowelsuit.purerules.context;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.context.condition.ConditionContext;
import com.github.sneakytowelsuit.purerules.context.field.FieldContext;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
public class EngineContextService<TInput, TInputId> {
  private final ConditionContext<TInputId> conditionEvaluationContext;
  private final FieldContext<TInputId> fieldContext;
  private final Map<String, Integer> conditionCumulativeWeightMap = new HashMap<>();
  private final Function<TInput, TInputId> inputIdGetter;

  public EngineContextService(EngineMode engineMode, Function<TInput, TInputId> inputIdGetter, List<Condition<TInput>> conditions) {
    this.conditionEvaluationContext = new ConditionContext<>();
    this.fieldContext = new FieldContext<>();
    this.inputIdGetter = inputIdGetter;
    if (engineMode == EngineMode.PROBABILISTIC) {
      this.calculateWeights(conditions);
    }
  }

  private void calculateWeights(List<Condition<TInput>> conditions) {
    for (Condition<TInput> condition : conditions) {
      switch(condition) {
          case Rule<TInput, ?> v -> {
            this.conditionCumulativeWeightMap.put(v.getId(), v.getCumulativeWeight());
          }
          case RuleGroup<TInput> v -> {
            this.calculateWeights(v.getConditions());
            this.conditionCumulativeWeightMap.put(v.getId(), v.getCumulativeWeight());
          }
      }
    }
  }
}
