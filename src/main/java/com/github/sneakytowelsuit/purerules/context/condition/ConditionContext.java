package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public class ConditionContext<TInputId> {
  private final Map<ConditionContextKey<TInputId>, ConditionContextValue> conditionContextMap;

  public ConditionContext() {
    this.conditionContextMap = new ConcurrentHashMap<>();
  }
}
