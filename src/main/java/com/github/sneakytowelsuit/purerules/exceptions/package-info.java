/**
 * Exception types for error handling throughout the PureRules engine.
 *
 * <p>This package contains runtime exceptions that may be thrown during various engine operations.
 * All exceptions are unchecked (extending {@link RuntimeException}) to maintain clean API usage
 * while still providing detailed error information for debugging and monitoring.
 *
 * <p>Exception categories:
 * <ul>
 *   <li><strong>Serialization Errors:</strong> Problems converting rules/rule groups to/from JSON</li>
 *   <li><strong>Rule Processing Errors:</strong> Issues during rule evaluation or construction</li>
 *   <li><strong>Configuration Errors:</strong> Invalid engine or condition setup</li>
 * </ul>
 *
 * <p><strong>Exception Types:</strong>
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.exceptions.RuleSerializationException} - 
 *       Individual rule serialization failures</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.exceptions.RuleGroupSerializationException} - 
 *       Rule group serialization failures</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.exceptions.RuleGroupDeserializationException} - 
 *       Rule group deserialization failures</li>
 * </ul>
 *
 * <p><strong>Error Handling Best Practices:</strong>
 * <pre>{@code
 * try {
 *     RuleGroup<Person> group = serde.deserialize(jsonString);
 *     Map<String, Boolean> results = engine.evaluate(person);
 * } catch (RuleGroupDeserializationException e) {
 *     logger.error("Failed to parse rule group JSON: {}", e.getMessage(), e);
 *     // Handle invalid rule configuration
 * } catch (Exception e) {
 *     logger.error("Unexpected evaluation error: {}", e.getMessage(), e);
 *     // Handle unexpected errors
 * }
 * }</pre>
 *
 * <p><strong>Logging and Monitoring:</strong>
 * All exceptions include detailed messages and preserve stack traces from underlying causes,
 * making them suitable for logging, monitoring, and debugging in production environments.
 *
 * @see com.github.sneakytowelsuit.purerules.serialization
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 */
package com.github.sneakytowelsuit.purerules.exceptions;