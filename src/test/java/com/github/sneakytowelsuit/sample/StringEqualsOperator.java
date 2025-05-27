package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Field;
import com.github.sneakytowelsuit.rule.Operator;

public class StringEqualsOperator implements Operator<Input, String> {
    @Override
    public boolean test(Input input, Field<Input, String> field, String value) {
        return field.getFieldValueFunction().apply(input).equals(value);
    }
}
