package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

// spotless:off
/**
 * An operator that tests for string equality while ignoring case differences.
 *
 * <p>This operator performs case-insensitive string comparison using the {@link
 * String#equalsIgnoreCase(String)} method. This is useful when you need exact string matching but
 * want to ignore variations in capitalization.
 *
 * <p>Null values are handled safely - if either input or comparison value is null, the operator
 * returns false. Note that two null values will return false (unlike {@link EqualsOperator} which
 * returns true for two nulls).
 *
 * <p>Common use cases include:
 *
 * <ul>
 *   <li>User input validation where case variations are acceptable
 *   <li>Enum-like string matching with flexible casing
 *   <li>Configuration value matching
 *   <li>Status or category validation
 * </ul>
 */
// spotless:on
public class StringEqualsIgnoreCaseOperator implements Operator<String> {

  // spotless:off
  /**
   * Tests whether the input string equals the comparison string, ignoring case.
   *
   * @param input the field value (string) extracted from the input data
   * @param value the target string to compare against, case will be ignored
   * @return true if the strings are equal ignoring case, false otherwise (including when either
   *     value is null)
   */
  // spotless:on
  @Override
  public boolean test(String input, String value) {
    if (input == null || value == null) {
      return false;
    }
    return input.equalsIgnoreCase(value);
  }
}
