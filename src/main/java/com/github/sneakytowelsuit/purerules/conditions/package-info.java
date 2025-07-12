/**
 * Core rule and condition definitions for building evaluation logic.
 *
 * <p>This package defines the fundamental building blocks of the PureRules engine: conditions,
 * rules, and rule groups. These components work together to create complex evaluation logic that
 * can be applied to input data.
 *
 * <p>Core types:
 *
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.Condition} - Base interface for all
 *       evaluatable conditions
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.Rule} - Individual comparison rules
 *       with field, operator, and value
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.RuleGroup} - Groups of conditions
 *       with combinator logic
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.Field} - Field extractors for
 *       obtaining values from input data
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.Operator} - Comparison operators for
 *       rule evaluation
 * </ul>
 *
 * <p>Supporting types:
 *
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.Combinator} - AND/OR logic for rule
 *       groups
 *   <li>{@link com.github.sneakytowelsuit.purerules.conditions.Bias} - Default results for empty
 *       rule groups
 * </ul>
 *
 * <p>Rules and rule groups can be nested arbitrarily deep, allowing for complex conditional logic.
 * All conditions support weighting for probabilistic evaluation and provide unique identifiers for
 * result tracking.
 *
 * @see com.github.sneakytowelsuit.purerules.conditions.Condition
 * @see com.github.sneakytowelsuit.purerules.conditions.Rule
 * @see com.github.sneakytowelsuit.purerules.conditions.RuleGroup
 */
package com.github.sneakytowelsuit.purerules.conditions;
