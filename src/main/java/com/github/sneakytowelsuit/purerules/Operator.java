package com.github.sneakytowelsuit.purerules;

public interface Operator<V> {
  boolean test(V input, V value);
}
