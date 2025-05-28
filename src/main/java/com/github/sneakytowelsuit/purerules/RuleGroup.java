package com.github.sneakytowelsuit.purerules;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

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
 *
 * @param <TInput> Type of the input that is being evaluated
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public final class RuleGroup<TInput> implements Condition<TInput> {
    private static final String RULE_GROUP_ID_PREFIX = "rule-group-";
    @Builder.Default
    private final String id = RULE_GROUP_ID_PREFIX + UUID.randomUUID().toString();
    @Builder.Default
    private final List<Condition<TInput>> conditions = new LinkedList<>();
    @Builder.Default
    private final Combinator combinator = Combinator.AND;
    @Builder.Default
    private final boolean isInverted = false;
    @Builder.Default
    private final Bias bias = Bias.EXCLUSIVE;
    private final static EvaluationContextCache EVALUATION_CONTEXT_CACHE = EvaluationContextCache.getInstance();
    private final Function<TInput, Predicate<Condition<TInput>>> CONDITION_PREDICATE = input -> condition -> {
        boolean conditionResult = condition.evaluate(input);
        String id = condition.getId();
        EVALUATION_CONTEXT_CACHE.getEvaluationContext().getConditionResults().putIfAbsent(id, conditionResult);
        return conditionResult;
    };

    public boolean evaluate(TInput input) {
        EVALUATION_CONTEXT_CACHE.instantiateContext();
        if (conditions.isEmpty()) {
            boolean result = this.isInverted ^ this.getBias().isBiasResult();
            String id = this.getId();
            EVALUATION_CONTEXT_CACHE.getEvaluationContext().getConditionResults().putIfAbsent(id, result);
            return result;
        }
        List<RuleGroup<TInput>> complexRules = new LinkedList<>();
        List<Rule<TInput, ?>> simpleRules = new LinkedList<>();
        for (Condition<TInput> condition : this.getConditions()) {
            switch (condition) {
                case RuleGroup<TInput> ruleGroup -> complexRules.add(ruleGroup);
                case Rule<TInput, ?> rule -> simpleRules.add(rule);
            }
        }
        boolean result = switch (Optional.ofNullable(this.getCombinator()).orElse(Combinator.AND)) {
            case AND -> simpleRules.stream().allMatch(CONDITION_PREDICATE.apply(input))
            && complexRules.stream().allMatch(CONDITION_PREDICATE.apply(input));
            case OR -> simpleRules.stream().anyMatch(CONDITION_PREDICATE.apply(input))
                    || complexRules.stream().anyMatch(CONDITION_PREDICATE.apply(input));
        };
        boolean finalResult = this.isInverted() ^ result;
        EVALUATION_CONTEXT_CACHE.getEvaluationContext().getConditionResults().putIfAbsent(id, finalResult);
        return finalResult;
    }
}
