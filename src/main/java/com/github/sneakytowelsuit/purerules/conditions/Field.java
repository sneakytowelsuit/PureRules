package com.github.sneakytowelsuit.purerules.conditions;

import java.util.function.Function;

// spotless:off
/**
 * Represents a field extractor that provides a function to extract a value of type {@code TValue}
 * from an input of type {@code TInput}. Used by rules to obtain the value to be compared.
 *
 * <p>Fields are the core mechanism for extracting data from input objects for rule evaluation.
 * They encapsulate the logic for accessing specific properties or computed values from your
 * domain objects, providing type safety and reusability across rules.
 *
 * <p><strong>Implementation Examples:</strong>
 *
 * <p>Simple property extraction:
 * <pre>{@code
 * public class NameField implements Field<Person, String> {
 *     @Override
 *     public Function<Person, String> getFieldValueFunction() {
 *         return Person::getName;  // Method reference
 *     }
 * }
 * 
 * // Or using lambda
 * public class AgeField implements Field<Person, Integer> {
 *     @Override
 *     public Function<Person, Integer> getFieldValueFunction() {
 *         return person -> person.getAge();
 *     }
 * }
 * }</pre>
 *
 * <p>Complex computed fields:
 * <pre>{@code
 * public class FullNameField implements Field<Person, String> {
 *     @Override
 *     public Function<Person, String> getFieldValueFunction() {
 *         return person -> person.getFirstName() + " " + person.getLastName();
 *     }
 * }
 *
 * public class AdultStatusField implements Field<Person, Boolean> {
 *     @Override
 *     public Function<Person, Boolean> getFieldValueFunction() {
 *         return person -> person.getAge() >= 18;
 *     }
 * }
 * }</pre>
 *
 * <p>Nested object field extraction:
 * <pre>{@code
 * public class AddressCityField implements Field<Person, String> {
 *     @Override
 *     public Function<Person, String> getFieldValueFunction() {
 *         return person -> person.getAddress() != null 
 *             ? person.getAddress().getCity() 
 *             : null;
 *     }
 * }
 * }</pre>
 *
 * <p><strong>Usage in Rules:</strong>
 * <pre>{@code
 * Rule<Person, String> nameRule = Rule.<Person, String>builder()
 *     .field(new NameField())
 *     .operator(new EqualsOperator<>())
 *     .value("John")
 *     .build();
 * }</pre>
 *
 * @param <TInput> the type of input object from which to extract values
 * @param <TValue> the type of value extracted from the input
 * 
 * @see Rule
 * @see Operator
 */
// spotless:on
public interface Field<TInput, TValue> {
  // spotless:off
  /**
   * Returns a function that extracts the field value from the input.
   *
   * <p>This method should return a function that safely extracts the desired value from the
   * input object. The function should handle null inputs gracefully and return null if the
   * requested value cannot be extracted.
   *
   * <p><strong>Implementation Guidelines:</strong>
   * <ul>
   *   <li>Return null for null inputs rather than throwing exceptions
   *   <li>Handle missing or invalid nested properties safely
   *   <li>Use method references when possible for better performance
   *   <li>Keep extraction logic simple and focused on a single value
   * </ul>
   *
   * <p><strong>Example implementations:</strong>
   * <pre>{@code
   * // Simple property access
   * return Person::getAge;
   * 
   * // Safe nested property access
   * return person -> {
   *     if (person == null || person.getAddress() == null) {
   *         return null;
   *     }
   *     return person.getAddress().getZipCode();
   * };
   * 
   * // Computed value
   * return person -> person != null 
   *     ? person.getFirstName() + " " + person.getLastName()
   *     : null;
   * }</pre>
   *
   * @return a function mapping input to the field value, should return null for invalid inputs
   */
  // spotless:on
  Function<TInput, TValue> getFieldValueFunction();
}
