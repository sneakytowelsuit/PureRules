package com.github.sneakytowelsuit.purerules.exceptions;

/**
 * Exception thrown when a {@link com.github.sneakytowelsuit.purerules.conditions.RuleGroup} cannot
 * be serialized to JSON or other formats.
 *
 * <p>This runtime exception indicates problems during the serialization process, such as:
 *
 * <ul>
 *   <li>Invalid rule group structure that cannot be represented in the target format
 *   <li>Serialization library configuration issues
 *   <li>Underlying I/O problems during serialization
 *   <li>Custom field or operator types that cannot be serialized
 * </ul>
 *
 * <p>This exception is typically thrown by serialization utilities in the {@code
 * com.github.sneakytowelsuit.purerules.serialization} package.
 */
public class RuleGroupSerializationException extends RuntimeException {

  /**
   * Creates a new exception with the specified error message.
   *
   * @param message description of the serialization error
   */
  public RuleGroupSerializationException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with the specified error message and underlying cause.
   *
   * @param message description of the serialization error
   * @param cause the underlying exception that caused this serialization failure
   */
  public RuleGroupSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
