package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;
import com.github.sneakytowelsuit.purerules.Operator;

public class StringEqualsOperator implements Operator<String> {
    @Override
    public boolean test(String input, String value) {
        return input.equals(value);
    }
}
