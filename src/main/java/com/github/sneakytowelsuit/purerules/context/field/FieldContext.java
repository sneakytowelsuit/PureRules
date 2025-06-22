package com.github.sneakytowelsuit.purerules.context.field;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class FieldContext<TInputId> {
  /**
   * A map that holds the context for fields, where the key is a FieldContextKey and the value is an
   * Object. This allows for storing various types of field-related data in a thread-safe manner.
   */
  private Map<FieldContextKey<TInputId>, Object> fieldContextMap;

  public FieldContext() {
    this.fieldContextMap = new ConcurrentHashMap<>();
  }

}
