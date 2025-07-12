package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

/**
 * An operator that tests for inequality between two values, opposite of {@link EqualsOperator}.
 *
 * <p>This operator handles null values with the inverse logic of equality:
 *
 * <ul>
 *   <li>Two null values are considered equal, so returns false (they are not different)
 *   <li>One null and one non-null value are considered different, so returns true
 *   <li>Two non-null values are compared using {@link Object#equals(Object)} and the result is
 *       inverted
 * </ul>
 *
 * <p>Useful for exclusion rules where you want to match everything except specific values.
 *
 * @param <T> the type of values to compare
 */
public class NotEqualsOperator<T> implements Operator<T> {

  /**
   * Tests whether the input value is not equal to the comparison value.
   *
   * @param input the field value extracted from the input data
   * @param value the target value to compare against
   * @return true if the values are not equal, false if they are equal (including both being null)
   */
  @Override
  public boolean test(T input, T value) {
    if (input == null && value == null) {
      return false;
    }
    if (input == null || value == null) {
      return true;
    }
    return !input.equals(value);
  }
}
