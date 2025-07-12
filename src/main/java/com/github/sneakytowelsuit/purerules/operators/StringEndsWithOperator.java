package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

/**
 * An operator that tests whether a string ends with a specific suffix.
 *
 * <p>This operator uses the {@link String#endsWith(String)} method to check if the input string
 * concludes with the specified suffix. The comparison is case-sensitive.
 *
 * <p>Null values are handled safely - if either input or comparison value is null, the operator
 * returns false.
 *
 * <p>Common use cases include:
 *
 * <ul>
 *   <li>File extension validation
 *   <li>URL or path suffix matching
 *   <li>Domain or hostname validation
 *   <li>Code or identifier suffix checking
 * </ul>
 *
 * <p>For case-insensitive suffix matching, consider using {@link
 * StringContainsCaseInsensitiveOperator} or converting both values to lowercase before comparison.
 */
public class StringEndsWithOperator implements Operator<String> {

  /**
   * Tests whether the input string ends with the comparison string.
   *
   * @param input the field value (string) extracted from the input data
   * @param value the suffix to check for at the end of the input string
   * @return true if input ends with value, false otherwise (including when either value is null)
   */
  @Override
  public boolean test(String input, String value) {
    if (input == null || value == null) {
      return false;
    }
    return input.endsWith(value);
  }
}
