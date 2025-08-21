// spotless:off
/**
 * Built-in comparison operators for rule evaluation.
 *
 * <p>This package provides a comprehensive set of operators that can be used to compare field
 * values against target values in rules. All operators implement the {@link
 * com.github.sneakytowelsuit.purerules.conditions.Operator} interface and handle null values
 * safely.
 *
 * <p>Available operators:
 *
 * <ul>
 *   <li><strong>General comparison:</strong> {@link
 *       com.github.sneakytowelsuit.purerules.operators.EqualsOperator}, {@link
 *       com.github.sneakytowelsuit.purerules.operators.NotEqualsOperator}
 *   <li><strong>Numeric comparison:</strong> {@link
 *       com.github.sneakytowelsuit.purerules.operators.GreaterThanOperator}, {@link
 *       com.github.sneakytowelsuit.purerules.operators.LessThanOperator}
 *   <li><strong>String operations:</strong> {@link
 *       com.github.sneakytowelsuit.purerules.operators.StringEqualsIgnoreCaseOperator}, {@link
 *       com.github.sneakytowelsuit.purerules.operators.StringContainsCaseInsensitiveOperator},
 *       {@link com.github.sneakytowelsuit.purerules.operators.StringStartsWithOperator}, {@link
 *       com.github.sneakytowelsuit.purerules.operators.StringEndsWithOperator}
 * </ul>
 *
 * <p>All operators are designed to be reusable and thread-safe. Custom operators can be created by
 * implementing the {@link com.github.sneakytowelsuit.purerules.conditions.Operator} interface.
 *
 * @see com.github.sneakytowelsuit.purerules.conditions.Operator
 */
// spotless:on
package com.github.sneakytowelsuit.purerules.operators;
