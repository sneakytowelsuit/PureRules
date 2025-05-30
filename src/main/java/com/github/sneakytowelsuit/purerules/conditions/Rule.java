package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.context.EngineContext;
import com.github.sneakytowelsuit.purerules.utils.ConditionUtils;
import lombok.*;

import java.util.List;
import java.util.UUID;

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
