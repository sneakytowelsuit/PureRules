package com.github.sneakytowelsuit.purerules.context.engine;

import com.github.sneakytowelsuit.purerules.engine.EngineMode;

/**
 * Represents a key for the engine context, which consists of an input identifier and the engine mode.
 * The input identifier is typically the hash code of the input object, and the engine mode indicates
 * the type of evaluation (e.g., deterministic or probabilistic).
 * This key is used to retrieve or manage evaluation contexts within the engine.
 */
public record EngineContextKey<I>(I inputId) {
}
