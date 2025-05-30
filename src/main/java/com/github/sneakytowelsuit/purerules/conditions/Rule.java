package com.github.sneakytowelsuit.purerules.conditions;

import lombok.*;

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

    public boolean evaluate(TInput input) {
        assert this.getOperator() != null;
        assert this.getField() != null;
        assert this.getField().getFieldValueFunction() != null;

        return this.getOperator().test(this.getField().getFieldValueFunction().apply(input), this.getValue());
    }
}
