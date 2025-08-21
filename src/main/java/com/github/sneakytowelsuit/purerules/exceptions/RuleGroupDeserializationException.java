package com.github.sneakytowelsuit.purerules.exceptions;

// spotless:off
/**
 * Exception thrown when a {@link com.github.sneakytowelsuit.purerules.conditions.RuleGroup} cannot
 * be deserialized from JSON.
 *
 * <p>This runtime exception indicates problems during the deserialization process, such as:
 *
 * <ul>
 *   <li>Malformed or invalid JSON structure
 *   <li>Missing required fields in the serialized data
 *   <li>Invalid values that cannot be converted to the expected types
 *   <li>Referenced field or operator types that cannot be instantiated
 *   <li>Deserialization library configuration issues
 * </ul>
 *
 * <p>This exception is typically thrown by deserialization utilities in the {@code
 * com.github.sneakytowelsuit.purerules.serialization} package when loading rule groups from
 * external sources.
 */
// spotless:on
public class RuleGroupDeserializationException extends RuntimeException {

  // spotless:off
  /**
   * Creates a new exception with the specified error message.
   *
   * @param message description of the deserialization error
   */
  // spotless:on
  public RuleGroupDeserializationException(String message) {
    super(message);
  }

  // spotless:off
  /**
   * Creates a new exception with the specified error message and underlying cause.
   *
   * @param message description of the deserialization error
   * @param cause the underlying exception that caused this deserialization failure
   */
  // spotless:on
  public RuleGroupDeserializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
