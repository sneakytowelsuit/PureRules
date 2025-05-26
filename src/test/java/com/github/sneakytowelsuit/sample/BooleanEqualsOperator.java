package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Field;
import com.github.sneakytowelsuit.rule.Operator;
import com.github.sneakytowelsuit.rule.Value;

public class BooleanEqualsOperator implements Operator<Input, Boolean> {
    @Override
    public boolean test(Input input, Field<Input, Boolean> field, Value<Boolean> value) {
        return field.getFieldValueFunction().apply(input).equals(value.getValue());
    }
}
