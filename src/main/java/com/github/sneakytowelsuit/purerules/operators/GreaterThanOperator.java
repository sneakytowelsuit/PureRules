package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

/**
 * An operator that tests whether the input value is greater than the comparison value.
 *
 * <p>This operator works with any type that implements {@link Comparable} and uses the {@link
 * Comparable#compareTo(Object)} method for comparison. The operator returns true if the input value
 * is greater than the comparison value.
 *
 * <p>Null values are handled safely - if either input or comparison value is null, the operator
 * returns false.
 *
 * <p>Common use cases include:
 *
 * <ul>
 *   <li>Numeric comparisons (integers, decimals, etc.)
 *   <li>Date and time comparisons
 *   <li>String lexicographical comparisons
 *   <li>Custom comparable objects
 * </ul>
 *
 * @param <T> the type of values to compare, must implement {@link Comparable}
 */
public class GreaterThanOperator<T extends Comparable<T>> implements Operator<T> {

  /**
   * Tests whether the input value is greater than the comparison value.
   *
   * @param input the field value extracted from the input data
   * @param value the target value to compare against
   * @return true if input > value, false otherwise (including when either value is null)
   */
  @Override
  public boolean test(T input, T value) {
    if (input == null || value == null) {
      return false;
    }
    return input.compareTo(value) > 0;
  }
}
