package com.github.sneakytowelsuit.purerules.context.condition;

import java.util.List;

public record ConditionContextKey<TInputId>(TInputId inputId, String conditionId) {}
