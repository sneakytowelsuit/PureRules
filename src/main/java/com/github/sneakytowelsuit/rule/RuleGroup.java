package com.github.sneakytowelsuit.rule;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

/**
 * An implementation of a Rule Group that has the following default opinions:
 * <ul>
 *     <li><strong>Conditions: </strong> Default to an empty list</li>
 *     <li><strong>Combinator: </strong> Default to <code>AND</code></li>
 *     <li><strong>Inverted: </strong> Default to <code>false</code></li>
 *     <li><strong>Bias: </strong> Default to <code>PESSIMISTIC</code></li>
 * </ul>
 * These defaults are in place to prevent evaluation from being too permissive, but can easily be overridden
 * through the builder methods.
 * @param <TInput> Type of the input that is being evaluated
 */
@Builder
@Getter
public final class RuleGroup<TInput> implements Evaluator<TInput> {
  @Builder.Default
  private final List<Evaluator<TInput>> conditions =  new LinkedList<>();
  @Builder.Default
  private final Combinator combinator = Combinator.AND;
  @Builder.Default
  private final boolean isInverted = false;
  @Builder.Default
  private final Bias bias = Bias.PESSIMISTIC;

  public boolean evaluate(TInput input) {
    if(conditions.isEmpty()) {
      boolean result = switch(this.getBias()) {
        case OPTIMISTIC ->  true;
        case PESSIMISTIC -> false;
      };
      return this.isInverted ^ result;
    }
    List<RuleGroup<TInput>> complexRules = new LinkedList<>();
    List<Rule<TInput, ?>> simpleRules = new LinkedList<>();
    for (Evaluator<TInput> condition : this.getConditions()) {
      switch (condition) {
        case RuleGroup<TInput> ruleGroup -> complexRules.add(ruleGroup);
        case Rule<TInput, ?>  rule -> simpleRules.add(rule);
      }
    }
    boolean result = switch (Optional.ofNullable(this.getCombinator()).orElse(Combinator.AND)) {
      case AND -> simpleRules.stream().allMatch(r ->
              r.test(input)) &&  complexRules.stream().allMatch(r -> r.evaluate(input)
      );
      case OR -> simpleRules.stream().anyMatch(r ->
              r.test(input)) ||   complexRules.stream().anyMatch(r -> r.evaluate(input)
      );
    };
    return this.isInverted() ^ result;
  }
}
