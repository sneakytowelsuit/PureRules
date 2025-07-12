package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

/**
 * An operator that tests whether a string contains another string, ignoring case differences.
 *
 * <p>This operator performs case-insensitive substring matching by converting both the input and
 * comparison strings to lowercase before checking for containment. This is useful for flexible text
 * matching where case variations should be ignored.
 *
 * <p>Null values are handled safely - if either input or comparison value is null, the operator
 * returns false.
 *
 * <p>Common use cases include:
 *
 * <ul>
 *   <li>Search functionality where case doesn't matter
 *   <li>Category matching with flexible naming
 *   <li>Description or comment filtering
 *   <li>Tag or keyword matching
 * </ul>
 *
 * <p>For case-sensitive containment checking, use a custom operator or combine {@link
 * String#contains(CharSequence)} with {@link EqualsOperator}.
 */
public class StringContainsCaseInsensitiveOperator implements Operator<String> {

  /**
   * Tests whether the input string contains the comparison string, ignoring case.
   *
   * @param input the field value (string) extracted from the input data
   * @param value the substring to search for, case will be ignored
   * @return true if input contains value (case-insensitive), false otherwise (including when either
   *     value is null)
   */
  @Override
  public boolean test(String input, String value) {
    if (input == null || value == null) {
      return false;
    }
    return input.toLowerCase().contains(value.toLowerCase());
  }
}
