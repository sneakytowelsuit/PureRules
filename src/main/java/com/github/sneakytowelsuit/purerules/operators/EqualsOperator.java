package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

// spotless:off
/**
 * An operator that tests for equality between two values using their {@code equals} method.
 *
 * <p>This operator handles null values gracefully:
 *
 * <ul>
 *   <li>Two null values are considered equal (returns true)
 *   <li>One null and one non-null value are considered not equal (returns false)
 *   <li>Two non-null values are compared using {@link Object#equals(Object)}
 * </ul>
 *
 * <p>This is the most commonly used operator for exact value matching in rules.
 *
 * @param <T> the type of values to compare
 */
// spotless:on
public class EqualsOperator<T> implements Operator<T> {

  // spotless:off
  /**
   * Tests whether the input value equals the comparison value.
   *
   * @param input the field value extracted from the input data
   * @param value the target value to compare against
   * @return true if the values are equal (including both being null), false otherwise
   */
  // spotless:on
  @Override
  public boolean test(T input, T value) {
    if (input == null && value == null) {
      return true; // Both are null, considered equal
    }
    if (input == null || value == null) {
      return false; // One is null, the other is not, considered not equal
    }
    return input.equals(value);
  }
}
