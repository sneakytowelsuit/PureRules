package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.context.EngineContext;
import com.github.sneakytowelsuit.purerules.utils.ConditionUtils;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * Represents a single rule condition that evaluates an input using a field extractor,
 * an operator, and a comparison value. The rule is uniquely identified and can be
 * evaluated in the context of a rule group or independently.
 *
 * @param <TInput>  the type of input to evaluate
 * @param <TValue>  the type of value extracted and compared
 */
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public final class Rule<TInput, TValue> implements Condition<TInput> {
    private final static String RULE_ID_PREFIX = "rule-";
    @Builder.Default
    private final String id = RULE_ID_PREFIX + UUID.randomUUID().toString();
    private final Field<TInput, TValue> field;
    private final Operator<TValue> operator;
    private final TValue value;

    /**
     * Evaluates this rule against the given input, using the provided parent ID path and thread ID.
     * Stores the result in the deterministic evaluation context.
     *
     * @param input        the input to evaluate
     * @param parentIdPath the parent ID path for context (can be null)
     * @param threadId     the thread ID for context (must not be null)
     * @return true if the rule condition is satisfied, false otherwise
     * @throws AssertionError if required fields or parameters are null
     */
    public boolean evaluate(TInput input, List<String> parentIdPath, Long threadId) {
        assert this.getOperator() != null;
        assert this.getField() != null;
        assert this.getField().getFieldValueFunction() != null;
        assert threadId != null;

        List<String> idPath = ConditionUtils.getIdPath(this, parentIdPath);
        boolean result = this.getOperator().test(this.getField().getFieldValueFunction().apply(input), this.getValue());
        EngineContext.getInstance()
                .getDeterministicEvaluationContext(threadId)
                .getConditionResults()
                .put(idPath, result);
        return result;
    }
}
