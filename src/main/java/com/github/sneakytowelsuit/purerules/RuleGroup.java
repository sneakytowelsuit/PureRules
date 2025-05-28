package com.github.sneakytowelsuit.purerules;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public final class RuleGroup<TInput> implements Condition<TInput> {
  @Builder.Default
  private final List<Condition<TInput>> conditions =  new LinkedList<>();
  @Builder.Default
  private final Combinator combinator = Combinator.AND;
  @Builder.Default
  private final boolean isInverted = false;
  @Builder.Default
  private final Bias bias = Bias.EXCLUSIVE;

  public boolean evaluate(TInput input) {
    if(conditions.isEmpty()) {
      return this.isInverted ^ this.getBias().isBiasResult();
    }
    List<RuleGroup<TInput>> complexRules = new LinkedList<>();
    List<Rule<TInput, ?>> simpleRules = new LinkedList<>();
    for (Condition<TInput> condition : this.getConditions()) {
      switch (condition) {
        case RuleGroup<TInput> ruleGroup -> complexRules.add(ruleGroup);
        case Rule<TInput, ?>  rule -> simpleRules.add(rule);
      }
    }
    boolean result = switch (Optional.ofNullable(this.getCombinator()).orElse(Combinator.AND)) {
      case AND -> simpleRules.stream().parallel().allMatch(r ->
              r.test(input)) &&  complexRules.stream().parallel().allMatch(r -> r.evaluate(input)
      );
      case OR -> simpleRules.stream().parallel().anyMatch(r ->
              r.test(input)) ||   complexRules.stream().parallel().anyMatch(r -> r.evaluate(input)
      );
    };
    return this.isInverted() ^ result;
  }
}
