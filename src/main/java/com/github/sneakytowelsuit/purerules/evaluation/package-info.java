/**
 * Evaluation service implementations for different engine modes.
 *
 * <p>This package contains the core evaluation logic for the PureRules engine, with different
 * implementations supporting various evaluation strategies:
 *
 * <ul>
 *   <li><strong>Deterministic Evaluation:</strong> Strict boolean logic with exact rule matching
 *   <li><strong>Probabilistic Evaluation:</strong> Weighted scoring with probability thresholds
 * </ul>
 *
 * <p>All evaluation services implement the {@link
 * com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService} interface and work with the
 * {@link com.github.sneakytowelsuit.purerules.context.EngineContextService} to manage field value
 * caching and evaluation state tracking.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Performance optimization through field value caching
 *   <li>Comprehensive evaluation context for debugging and analysis
 *   <li>Support for nested rule groups with recursive evaluation
 *   <li>Proper handling of edge cases like empty rule groups and null values
 * </ul>
 *
 * <p>Evaluation services are designed to be thread-safe and reusable across multiple evaluation
 * operations.
 *
 * @see com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService
 * @see com.github.sneakytowelsuit.purerules.evaluation.DeterministicEvaluationService
 * @see com.github.sneakytowelsuit.purerules.evaluation.ProbabilisticEvaluationService
 */
package com.github.sneakytowelsuit.purerules.evaluation;
