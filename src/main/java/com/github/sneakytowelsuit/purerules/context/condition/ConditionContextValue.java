package com.github.sneakytowelsuit.purerules.context.condition;

public sealed interface ConditionContextValue permits RuleContextValue, RuleGroupContextValue {
  public Integer getResult();

  public Integer getMaximumResult();
}
