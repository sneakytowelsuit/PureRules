package com.github.sneakytowelsuit.rule;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class RuleGroup<TInput> implements Evaluator<TInput> {
  private final String id;
  private final Integer depth;
  private final List<Evaluator<TInput>> conditions;
  private final Combinator combinator;
  private final boolean isInverted;
}
