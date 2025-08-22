package com.github.sneakytowelsuit.purerules.context.condition;

import java.time.Duration;

public sealed interface ConditionContextValue permits RuleContextValue, RuleGroupContextValue {
  public Integer getResult();

  public Integer getMaximumResult();

  public String getId();

  public Duration getEvaluationDuration();
}
