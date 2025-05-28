package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;
import com.github.sneakytowelsuit.purerules.Operator;

public class BooleanEqualsOperator implements Operator<Input, Boolean> {
    @Override
    public boolean test(Input input, Field<Input, Boolean> field, Boolean value) {
        return field.getFieldValueFunction().apply(input).equals(value);
    }
}
