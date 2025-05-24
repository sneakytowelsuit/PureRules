package com.github.sneakytowelsuit.rule;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class Value<TValue> {
  private final TValue value;
}
