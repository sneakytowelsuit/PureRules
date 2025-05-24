package com.github.sneakytowelsuit.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

  public boolean evaluate(TInput input) {
    List<RuleGroup<TInput>> complexRules = new LinkedList<>();
    List<Rule<TInput, ?>> simpleRules = new LinkedList<>();
    for (Evaluator<TInput> condition : conditions) {
      switch (condition) {
        case RuleGroup<TInput> ruleGroup -> complexRules.add(ruleGroup);
        case Rule<TInput, ?>  rule ->   simpleRules.add(rule);
      }
    }
    boolean result = switch (this.getCombinator()) {
      case AND -> simpleRules.stream().allMatch(r -> r.test(input)) &&  complexRules.stream().allMatch(r -> r.evaluate(input));
      case OR -> simpleRules.stream().anyMatch(r -> r.test(input)) ||   complexRules.stream().anyMatch(r -> r.evaluate(input));
    };
    return this.isInverted() ^ result;
  }
}
