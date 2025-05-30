package com.github.sneakytowelsuit.purerules.conditions;

public sealed interface Condition<InputType> permits Rule, RuleGroup {
    public String getId();
}
