package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;
import com.github.sneakytowelsuit.purerules.Operator;

public class StringEqualsOperator implements Operator<Input, String> {
    @Override
    public boolean test(Input input, Field<Input, String> field, String value) {
        return field.getFieldValueFunction().apply(input).equals(value);
    }
}
