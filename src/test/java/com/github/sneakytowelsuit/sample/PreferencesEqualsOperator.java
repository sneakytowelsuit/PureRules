package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Operator;

public class PreferencesEqualsOperator implements Operator<Preferences> {
    @Override
    public boolean test(Preferences input, Preferences value) {
        return input.equals(value);
    }
}
