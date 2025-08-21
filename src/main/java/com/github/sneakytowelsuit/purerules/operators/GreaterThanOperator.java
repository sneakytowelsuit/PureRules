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
 * <p><strong>Common use cases:</strong>
 * <ul>
 *   <li>Numeric comparisons (integers, decimals, etc.)</li>
 *   <li>Date and time comparisons</li>
 *   <li>String lexicographical comparisons</li>
 *   <li>Custom comparable objects</li>
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 * <pre>{@code
 * // Age validation rule
 * Rule<Person, Integer> adultRule = Rule.<Person, Integer>builder()
 *     .field(new AgeField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(18)
 *     .build();
 *
 * // Salary requirement rule  
 * Rule<Employee, BigDecimal> salaryRule = Rule.<Employee, BigDecimal>builder()
 *     .field(new SalaryField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(new BigDecimal("50000"))
 *     .build();
 *     
 * // Date comparison rule
 * Rule<Event, LocalDate> futureEventRule = Rule.<Event, LocalDate>builder()
 *     .field(new EventDateField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(LocalDate.now())
 *     .build();
 * }</pre>
 *
 * @param <T> the type of values to compare, must implement {@link Comparable}
 * 
 * @see LessThanOperator
 * @see EqualsOperator
 * @see Comparable
 */
public class GreaterThanOperator<T extends Comparable<T>> implements Operator<T> {

  /**
   * Tests whether the input value is greater than the comparison value.
   *
   * @param input the field value extracted from the input data
   * @param value the target value to compare against
   * @return true if input is greater than value, false otherwise (including when either value is null)
   */
  @Override
  public boolean test(T input, T value) {
    if (input == null || value == null) {
      return false;
    }
    return input.compareTo(value) > 0;
  }
}
