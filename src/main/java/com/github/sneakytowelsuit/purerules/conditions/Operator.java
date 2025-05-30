package com.github.sneakytowelsuit.purerules.conditions;

public interface Operator<V> {
  boolean test(V input, V value);
}
