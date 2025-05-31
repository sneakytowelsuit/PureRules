package com.github.sneakytowelsuit.purerules.conditions;

import java.util.function.Function;

/**
 * Represents a field extractor that provides a function to extract a value of type {@code TValue}
 * from an input of type {@code TInput}. Used by rules to obtain the value to be compared.
 *
 * @param <TInput> the type of input object
 * @param <TValue> the type of value extracted from the input
 */
public interface Field<TInput, TValue> {
  /**
   * Returns a function that extracts the field value from the input.
   *
   * @return a function mapping input to the field value
   */
  Function<TInput, TValue> getFieldValueFunction();
}
