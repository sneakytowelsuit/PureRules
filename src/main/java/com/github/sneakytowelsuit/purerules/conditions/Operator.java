package com.github.sneakytowelsuit.purerules.conditions;

/**
 * Represents a comparison operator that tests a value extracted from input against an expected
 * value. Used by rules to perform logical comparisons.
 *
 * <p>Operators define the comparison logic between field values and rule target values. They
 * provide the core evaluation mechanism that determines whether a rule condition is satisfied.
 * All operators should handle null values safely and consistently.
 *
 * <p><strong>Built-in Operators Available:</strong>
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.EqualsOperator} - Exact equality</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.NotEqualsOperator} - Inequality</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.GreaterThanOperator} - Numeric comparison</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.LessThanOperator} - Numeric comparison</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.StringContainsCaseInsensitiveOperator} - Text matching</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.StringStartsWithOperator} - Text prefix matching</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.operators.StringEndsWithOperator} - Text suffix matching</li>
 * </ul>
 *
 * <p><strong>Custom Operator Examples:</strong>
 *
 * <p>Simple custom operator:
 * <pre>{@code
 * public class ContainsOperator implements Operator<String> {
 *     @Override
 *     public boolean test(String input, String value) {
 *         if (input == null || value == null) {
 *             return false;
 *         }
 *         return input.contains(value);
 *     }
 * }
 * }</pre>
 *
 * <p>Numeric range operator:
 * <pre>{@code
 * public class BetweenOperator implements Operator<Integer> {
 *     @Override
 *     public boolean test(Integer input, Integer value) {
 *         // Expects value to be the upper bound, configure lower bound separately
 *         if (input == null || value == null) {
 *             return false;
 *         }
 *         return input >= getLowerBound() && input <= value;
 *     }
 *     
 *     private int getLowerBound() { return 0; } // Or configure as needed
 * }
 * }</pre>
 *
 * <p>Collection-based operator:
 * <pre>{@code
 * public class InListOperator<T> implements Operator<T> {
 *     private final List<T> allowedValues;
 *     
 *     public InListOperator(List<T> allowedValues) {
 *         this.allowedValues = allowedValues != null ? allowedValues : Collections.emptyList();
 *     }
 *     
 *     @Override
 *     public boolean test(T input, T value) {
 *         // value parameter ignored, using constructor list instead
 *         return allowedValues.contains(input);
 *     }
 * }
 * }</pre>
 *
 * <p><strong>Usage in Rules:</strong>
 * <pre>{@code
 * Rule<Person, Integer> ageRule = Rule.<Person, Integer>builder()
 *     .field(new AgeField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(18)
 *     .build();
 *
 * Rule<Person, String> nameRule = Rule.<Person, String>builder()
 *     .field(new NameField())
 *     .operator(new ContainsOperator())
 *     .value("John")
 *     .build();
 * }</pre>
 *
 * @param <TValue> the type of value to compare
 * 
 * @see Rule
 * @see Field
 */
public interface Operator<TValue> {
  /**
   * Tests the input value against the expected value using this operator's logic.
   *
   * <p>This method performs the core comparison logic for the operator. Implementations should
   * handle null values gracefully and return consistent results for edge cases.
   *
   * <p><strong>Implementation Guidelines:</strong>
   * <ul>
   *   <li>Handle null inputs and values safely (typically return false for null mismatches)</li>
   *   <li>Return consistent results for identical inputs across multiple calls</li>
   *   <li>Document any special null-handling behavior in the operator's class JavaDoc</li>
   *   <li>Consider performance implications for frequently used operators</li>
   *   <li>Throw runtime exceptions only for truly exceptional conditions, not for normal null inputs</li>
   * </ul>
   *
   * <p><strong>Common Patterns:</strong>
   * <pre>{@code
   * // Null-safe equality check
   * if (input == null && value == null) return true;
   * if (input == null || value == null) return false;
   * return input.equals(value);
   * 
   * // Null-safe numeric comparison
   * if (input == null || value == null) return false;
   * return input.compareTo(value) > 0;
   * 
   * // Null-safe string operation
   * if (input == null || value == null) return false;
   * return input.toLowerCase().contains(value.toLowerCase());
   * }</pre>
   *
   * @param input the value extracted from the input data by the associated field
   * @param value the target value from the rule definition to compare against
   * @return true if the comparison is satisfied according to this operator's logic, false otherwise
   */
  boolean test(TValue input, TValue value);
}
