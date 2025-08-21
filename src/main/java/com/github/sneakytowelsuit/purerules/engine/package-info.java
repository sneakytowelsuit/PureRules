// spotless:off
/**
 * Core engine implementation and configuration for the PureRules engine.
 *
 * <p>This package contains the main engine class and related configuration types that control how
 * rules are evaluated. The engine supports two evaluation modes with different characteristics:
 *
 * <ul>
 *   <li><strong>Deterministic mode:</strong> Strict boolean evaluation using exact rule matching
 *   <li><strong>Probabilistic mode:</strong> Weighted scoring with probability thresholds for fuzzy
 *       matching
 * </ul>
 *
 * <p>The engine is immutable after construction, ensuring thread safety and predictable behavior.
 * Rules and configuration are provided at instantiation time and cannot be modified afterward.
 *
 * <p>Key classes:
 *
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.engine.PureRulesEngine} - Main engine
 *       implementation
 *   <li>{@link com.github.sneakytowelsuit.purerules.engine.EngineMode} - Evaluation mode
 *       configuration
 * </ul>
 *
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 */
// spotless:on
package com.github.sneakytowelsuit.purerules.engine;
