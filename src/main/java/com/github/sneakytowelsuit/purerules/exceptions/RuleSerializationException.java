package com.github.sneakytowelsuit.purerules.exceptions;

/**
 * Exception thrown when a {@link com.github.sneakytowelsuit.purerules.conditions.Rule} cannot be
 * serialized to JSON.
 *
 * <p>This runtime exception indicates problems during the rule serialization process, such as:
 *
 * <ul>
 *   <li>Custom field types that cannot be serialized
 *   <li>Operator implementations that are not serializable
 *   <li>Value types that cannot be converted to the target format
 *   <li>Serialization library configuration issues
 *   <li>Underlying I/O problems during serialization
 * </ul>
 *
 * <p>This exception is typically thrown by serialization utilities when processing individual rules
 * within rule groups or standalone rule serialization operations.
 */
public class RuleSerializationException extends RuntimeException {

  /**
   * Creates a new exception with the specified error message and underlying cause.
   *
   * @param message description of the rule serialization error
   * @param cause the underlying exception that caused this serialization failure
   */
  public RuleSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
