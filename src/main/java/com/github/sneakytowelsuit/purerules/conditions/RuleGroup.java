package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.context.*;
import com.github.sneakytowelsuit.purerules.engine.EngineMode;
import com.github.sneakytowelsuit.purerules.engine.EvaluationMode;
import com.github.sneakytowelsuit.purerules.utils.ConditionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
@Setter
@EqualsAndHashCode
public final class RuleGroup<TInput> implements Condition<TInput> {
  private static final String RULE_GROUP_ID_PREFIX = "rule-group-";
  @Builder.Default private final String id = RULE_GROUP_ID_PREFIX + UUID.randomUUID().toString();
  @Builder.Default private final List<Condition<TInput>> conditions = new ArrayList<>();
  @Builder.Default private final Combinator combinator = Combinator.AND;
  @Builder.Default private final boolean isInverted = false;
  @Builder.Default private final Integer priority = 1;

  /** Bias to use when the group contains no conditions. Defaults to EXCLUSIVE (pessimistic). */
  @Builder.Default private final Bias bias = Bias.EXCLUSIVE;

  /**
   * Evaluates this rule group against the given input. Uses the current thread ID and the group's
   * ID as the evaluation context.
   *
   * @param input the input to evaluate
   * @return true if the group condition is satisfied, false otherwise
   */
  public boolean evaluate(TInput input, EvaluationMode evaluationMode, EvaluationContext<?> evaluationContext) {
    return evaluateConditions(
        input,
        ConditionUtils.getIdPath(this, null),
        Thread.currentThread().threadId(),
        evaluationMode,
        evaluationContext
    );
  }

  /**
   * Evaluates all conditions in this group using the specified combinator, inversion, and bias.
   * Results are stored in the deterministic evaluation context.
   *
   * @param input the input to evaluate
   * @param idPath the parent ID path for context
   * @param threadId the thread ID for context
   * @return true if the group condition is satisfied, false otherwise
   */
  private boolean evaluateConditions(
          TInput input,
          List<String> idPath,
          Long threadId,
          EvaluationMode evaluationMode,
          EvaluationContext<?> evaluationContext
  ) {
    if (conditions.isEmpty()) {
      return this.handleEmptyConditions(evaluationMode, evaluationContext, threadId, idPath);
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
            input, this.getCombinator(), simpleRules, complexRules, idPath, threadId, evaluationMode, evaluationContext);
    return result;
  }

  private boolean handleConditions(
      TInput input,
      Combinator combinator,
      List<Rule<TInput, ?>> simpleRules,
      List<RuleGroup<TInput>> complexRules,
      List<String> idPath,
      Long threadId,
      EvaluationMode evaluationMode,
      EvaluationContext<?> evaluationContext) {
    boolean result = switch (Optional.ofNullable(combinator).orElse(Combinator.AND)) {
          case AND ->
              simpleRules.stream().allMatch(r -> r.evaluate(input, idPath, threadId, evaluationMode, evaluationContext))
                  && complexRules.stream()
                      .allMatch(r -> r.evaluateConditions(input, idPath, threadId, evaluationMode, evaluationContext));
          case OR ->
              simpleRules.stream().anyMatch(r -> r.evaluate(input, idPath, threadId, evaluationMode, evaluationContext))
                  || complexRules.stream()
                      .anyMatch(r -> r.evaluateConditions(input, idPath, threadId, evaluationMode, evaluationContext));
        }
        ^ this.isInverted;
    updateContext(evaluationMode, evaluationContext, idPath, threadId, result);
    return result;
  }

  private boolean handleEmptyConditions(
          EvaluationMode evaluationMode,
          EvaluationContext<?> evaluationContext,
          Long threadId,
          List<String> idPath
  ) {
    boolean result = this.isInverted ^ this.getBias().isBiasResult();
    updateContext(evaluationMode, evaluationContext, idPath, threadId, result);
    return result;
  }

  private void updateContext(
          EvaluationMode evaluationMode,
            EvaluationContext<?> evaluationContext,
          List<String> idPath,
          Long threadId,
          boolean result
  ) {
    if (evaluationMode == EvaluationMode.DEBUG) {
      switch (evaluationContext) {
        case DeterministicEvaluationContext deterministicEvaluationContext -> {
          deterministicEvaluationContext
                  .getConditionResults()
                  .put(new ContextKey(idPath, threadId), result);
        }
        case ProbabilisticEvaluationContext probabilisticEvaluationContext -> {
          probabilisticEvaluationContext
                  .getConditionResults()
                  .put(
                          new ContextKey(idPath, threadId),
                          ProbabilisticRuleGroupEvaluationContextValue.builder()
                                  .build()
                  );
        }
      }
    }
  }
}
