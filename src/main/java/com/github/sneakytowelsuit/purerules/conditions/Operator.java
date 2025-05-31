package com.github.sneakytowelsuit.purerules.conditions;

/**
 * Represents a comparison operator that tests a value extracted from input
 * against an expected value. Used by rules to perform logical comparisons.
 *
 * @param <V> the type of value to compare
 */
public interface Operator<V> {
  /**
   * Tests the input value against the expected value using this operator's logic.
   *
   * @param input the value extracted from the input
   * @param value the value to compare against
   * @return true if the comparison is satisfied, false otherwise
   */
  boolean test(V input, V value);
}
