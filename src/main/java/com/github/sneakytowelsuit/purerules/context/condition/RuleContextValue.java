package com.github.sneakytowelsuit.purerules.context.condition;

import lombok.Builder;

@Builder
public final class RuleContextValue<V> implements ConditionContextValue {
    private String ruleId;
    private V fieldValue;
    private V valueValue;
    private String operator;
    private Boolean result;
}
