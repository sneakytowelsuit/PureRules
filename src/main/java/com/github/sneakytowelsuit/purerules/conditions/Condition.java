package com.github.sneakytowelsuit.purerules.conditions;
/**
 * Represents a logical condition that can be evaluated.
 * This interface is sealed and only permits {@link Rule} and {@link RuleGroup} as implementations.
 *
 * @param <InputType> the type of input to evaluate
 */
public sealed interface Condition<InputType> permits Rule, RuleGroup {
    public String getId();
}
