package com.github.sneakytowelsuit.purerules.context.condition;

import java.time.Duration;

import com.github.sneakytowelsuit.purerules.conditions.Bias;
import com.github.sneakytowelsuit.purerules.conditions.Combinator;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class RuleGroupContextValue implements ConditionContextValue {
  private String id;
  private Combinator combinator;
  private Bias bias;
  private Integer maximumResult;
  private Integer result;
  private Duration evaluationDuration;
}
