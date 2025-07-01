package com.github.sneakytowelsuit.purerules.context.condition;

import com.github.sneakytowelsuit.purerules.conditions.Bias;
import com.github.sneakytowelsuit.purerules.conditions.Combinator;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class RuleGroupContextValue implements ConditionContextValue {
  private Combinator combinator;
  private Bias bias;
  private Integer maximumResult;
  private Integer result;
}
