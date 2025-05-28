package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;
import com.github.sneakytowelsuit.purerules.Operator;

public class BooleanEqualsOperator implements Operator<Boolean> {
    @Override
    public boolean test(Boolean input, Boolean value) {
        return input.equals(value);
    }
}
