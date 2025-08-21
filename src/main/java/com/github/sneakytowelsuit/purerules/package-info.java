/**
 * PureRules: A flexible JVM-based rules engine for modern engineering teams.
 *
 * <p>This package provides the core API for the PureRules engine, offering a balance between
 * generic user-defined rules and robust lifecycle control. The engine supports both deterministic
 * and probabilistic evaluation modes with comprehensive context management.
 *
 * <p>Key components:
 *
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.engine} - Core engine implementation
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions} - Rule and condition definitions
 *   <li>{@link com.github.sneakytowelsuit.purerules.evaluation} - Evaluation service
 *       implementations
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators} - Built-in comparison operators
 *   <li>{@link com.github.sneakytowelsuit.purerules.context} - Context management for caching and
 *       debugging
 *   <li>{@link com.github.sneakytowelsuit.purerules.serialization} - JSON serialization support
 *   <li>{@link com.github.sneakytowelsuit.purerules.exceptions} - Exception types for error
 *       handling
 * </ul>
 *
 * <p>The engine is designed for:
 *
 * <ul>
 *   <li>High performance with field value caching and optimized evaluation
 *   <li>Type safety with generic type parameters throughout the API
 *   <li>Flexibility with support for custom fields, operators, and evaluation strategies
 *   <li>Debugging and analysis with comprehensive evaluation context tracking
 *   <li>External rule management through JSON serialization capabilities
 * </ul>
 *
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 * @see com.github.sneakytowelsuit.purerules.conditions.RuleGroup
 * @see com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService
 */
package com.github.sneakytowelsuit.purerules;
