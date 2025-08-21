package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

// spotless:off
/**
 * An operator that tests whether a string starts with a specific prefix.
 *
 * <p>This operator uses the {@link String#startsWith(String)} method to check if the input string
 * begins with the specified prefix. The comparison is case-sensitive.
 *
 * <p>Null values are handled safely - if either input or comparison value is null, the operator
 * returns false.
 *
 * <p>Common use cases include:
 *
 * <ul>
 *   <li>URL or path prefix matching
 *   <li>Code or identifier prefix validation
 *   <li>Category or classification matching
 *   <li>Protocol or scheme checking
 * </ul>
 *
 * <p>For case-insensitive prefix matching, consider using {@link
 * StringContainsCaseInsensitiveOperator} or converting both values to lowercase before comparison.
 */
// spotless:on
public class StringStartsWithOperator implements Operator<String> {

  // spotless:off
  /**
   * Tests whether the input string starts with the comparison string.
   *
   * @param input the field value (string) extracted from the input data
   * @param value the prefix to check for at the beginning of the input string
   * @return true if input starts with value, false otherwise (including when either value is null)
   */
  // spotless:on
  @Override
  public boolean test(String input, String value) {
    if (input == null || value == null) {
      return false;
    }
    return input.startsWith(value);
  }
}
