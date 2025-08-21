package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

/**
 * An operator that tests whether the input value is less than the comparison value.
 *
 * <p>This operator works with any type that implements {@link Comparable} and uses the {@link
 * Comparable#compareTo(Object)} method for comparison. The operator returns true if the input value
 * is less than the comparison value.
 *
 * <p>Null values are handled safely - if either input or comparison value is null, the operator
 * returns false.
 *
 * <p><strong>Common use cases:</strong>
 * <ul>
 *   <li>Numeric range validation (less than maximum values)</li>
 *   <li>Date and time boundary checks</li>
 *   <li>String lexicographical ordering</li>
 *   <li>Custom comparable object validation</li>
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 * <pre>{@code
 * // Maximum age limit rule
 * Rule<Person, Integer> seniorRule = Rule.<Person, Integer>builder()
 *     .field(new AgeField())
 *     .operator(new LessThanOperator<>())
 *     .value(65)
 *     .build();
 *
 * // Budget constraint rule
 * Rule<Purchase, BigDecimal> budgetRule = Rule.<Purchase, BigDecimal>builder()
 *     .field(new AmountField())
 *     .operator(new LessThanOperator<>())
 *     .value(new BigDecimal("1000"))
 *     .build();
 *     
 * // Deadline validation rule
 * Rule<Task, LocalDateTime> deadlineRule = Rule.<Task, LocalDateTime>builder()
 *     .field(new DueDateField())
 *     .operator(new LessThanOperator<>())
 *     .value(LocalDateTime.now().plusDays(7))  // Due within a week
 *     .build();
 * }</pre>
 *
 * @param <T> the type of values to compare, must implement {@link Comparable}
 * 
 * @see GreaterThanOperator
 * @see EqualsOperator
 * @see Comparable
 */
public class LessThanOperator<T extends Comparable<T>> implements Operator<T> {

  /**
   * Tests whether the input value is less than the comparison value.
   *
   * @param input the field value extracted from the input data
   * @param value the target value to compare against
   * @return true if input is less than value, false otherwise (including when either value is null)
   */
  @Override
  public boolean test(T input, T value) {
    if (input == null || value == null) {
      return false;
    }
    return input.compareTo(value) < 0;
  }
}
