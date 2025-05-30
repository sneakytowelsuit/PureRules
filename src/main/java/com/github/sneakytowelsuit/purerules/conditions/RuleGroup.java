package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.context.EngineContext;
import com.github.sneakytowelsuit.purerules.utils.ConditionUtils;
import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private final static EngineContext EVALUATION_CONTEXT_CACHE = EngineContext.getInstance();

    public boolean evaluate(TInput input) {
        Long threadId = Thread.currentThread().threadId();
        return evaluateConditions(input, threadId, ConditionUtils.getIdPath(this,null));
    }

    private boolean evaluateConditions(TInput input, Long threadId, List<String> parentIdPath) {
        EVALUATION_CONTEXT_CACHE.instantiateEvaluationContext(threadId);
        if (conditions.isEmpty()) {
            boolean result = this.isInverted ^ this.getBias().isBiasResult();
            EVALUATION_CONTEXT_CACHE.getEvaluationContext(threadId).getConditionResults().putIfAbsent(parentIdPath, result);
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
            case AND ->
                    simpleRules.stream().allMatch(r -> r.evaluate(input, parentIdPath, threadId))
                    && complexRules.stream().allMatch(r -> r.evaluateConditions(input, threadId, parentIdPath));
            case OR ->
                    simpleRules.stream().anyMatch(r -> r.evaluate(input, parentIdPath, threadId))
                    || complexRules.stream().anyMatch(r -> r.evaluateConditions(input, threadId, parentIdPath));
        };
        boolean finalResult = this.isInverted() ^ result;
        EVALUATION_CONTEXT_CACHE.getEvaluationContext(threadId).getConditionResults().putIfAbsent(parentIdPath, finalResult);
        return finalResult;
    }
}
