package com.github.sneakytowelsuit.purerules.operators;

import com.github.sneakytowelsuit.purerules.conditions.Operator;

public class StringEndsWithOperator implements Operator<String> {
    @Override
    public boolean test(String input, String value) {
        if (input == null || value == null) {
            return false;
        }
        return input.endsWith(value);
    }
}

