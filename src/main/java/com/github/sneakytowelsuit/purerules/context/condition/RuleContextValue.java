package com.github.sneakytowelsuit.purerules.context.condition;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class RuleContextValue<V> implements ConditionContextValue {
  private String id;
  private V fieldValue;
  private V valueValue;
  private String operator;
  private Integer result;
  private Integer maximumResult;
}
