package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.context.EngineContextImpl;
import com.github.sneakytowelsuit.purerules.utils.ConditionUtils;
import java.util.*;
import lombok.*;

/**
 * Represents a group of conditions (rules or nested rule groups) that are evaluated together using
 * a specified combinator (AND/OR), with optional inversion and bias. Each group is uniquely
 * identified. Default values are set to ensure safe evaluation, but can be overridden via the
 * builder.
 *
 * @param <TInput> the type of input to evaluate
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public final class RuleGroup<TInput> implements Condition<TInput> {
  private static final String RULE_GROUP_ID_PREFIX = "rule-group-";
  @Builder.Default private final String id = RULE_GROUP_ID_PREFIX + UUID.randomUUID().toString();
  @Builder.Default private final List<Condition<TInput>> conditions = new ArrayList<>();
  @Builder.Default private final Combinator combinator = Combinator.AND;
  @Builder.Default private final boolean isInverted = false;

  /** Bias to use when the group contains no conditions. Defaults to EXCLUSIVE (pessimistic). */
  @Builder.Default private final Bias bias = Bias.EXCLUSIVE;

  /**
   * Evaluates this rule group against the given input. Uses the current thread ID and the group's
   * ID as the evaluation context.
   *
   * @param input the input to evaluate
   * @return true if the group condition is satisfied, false otherwise
   */
  public boolean evaluate(TInput input) {
    Long threadId = Thread.currentThread().threadId();
    return evaluateConditions(
        input, ConditionUtils.getIdPath(this, null), Thread.currentThread().threadId());
  }

  /**
   * Evaluates all conditions in this group using the specified combinator, inversion, and bias.
   * Results are stored in the deterministic evaluation context.
   *
   * @param input the input to evaluate
   * @param parentIdPath the parent ID path for context
   * @param threadId the thread ID for context
   * @return true if the group condition is satisfied, false otherwise
   */
  private boolean evaluateConditions(TInput input, List<String> parentIdPath, Long threadId) {
    EngineContextImpl ctx = EngineContextImpl.getInstance();
    ctx.instantiateDeterministicEvaluationContext(threadId);
    if (conditions.isEmpty()) {
      return this.handleEmptyConditions(threadId, parentIdPath);
    }
    List<RuleGroup<TInput>> complexRules = new ArrayList<>();
    List<Rule<TInput, ?>> simpleRules = new ArrayList<>();
    for (Condition<TInput> condition : this.getConditions()) {
      switch (condition) {
        case RuleGroup<TInput> ruleGroup -> complexRules.add(ruleGroup);
        case Rule<TInput, ?> rule -> simpleRules.add(rule);
      }
    }
    boolean result =
        handleConditions(
            input, this.getCombinator(), simpleRules, complexRules, parentIdPath, threadId);
    ctx.getDeterministicEvaluationContext(threadId)
        .getConditionResults()
        .putIfAbsent(parentIdPath, result);
    return result;
  }

  private boolean handleConditions(
      TInput input,
      Combinator combinator,
      List<Rule<TInput, ?>> simpleRules,
      List<RuleGroup<TInput>> complexRules,
      List<String> parentIdPath,
      Long threadId) {
    return switch (Optional.ofNullable(combinator).orElse(Combinator.AND)) {
          case AND ->
              simpleRules.stream().allMatch(r -> r.evaluate(input, parentIdPath, threadId))
                  && complexRules.stream()
                      .allMatch(r -> r.evaluateConditions(input, parentIdPath, threadId));
          case OR ->
              simpleRules.stream().anyMatch(r -> r.evaluate(input, parentIdPath, threadId))
                  || complexRules.stream()
                      .anyMatch(r -> r.evaluateConditions(input, parentIdPath, threadId));
        }
        ^ this.isInverted;
  }

  private boolean handleEmptyConditions(Long threadId, List<String> parentIdPath) {
    boolean result = this.isInverted ^ this.getBias().isBiasResult();
    updateContext(threadId, parentIdPath, result);
    return result;
  }

  private void updateContext(Long threadId, List<String> parentIdPath, boolean result) {
    EngineContextImpl ctx = EngineContextImpl.getInstance();
    ctx.getDeterministicEvaluationContext(threadId)
        .getConditionResults()
        .putIfAbsent(parentIdPath, result);
  }
}
