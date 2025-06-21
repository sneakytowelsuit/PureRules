package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.List;

public record ConditionContextKey(
    /** The path to the condition in the rule hierarchy. */
    List<String> path,
    /** The ID of the current thread. */
    Long threadId) {}
