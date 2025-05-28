package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;
import com.github.sneakytowelsuit.purerules.Operator;

import java.util.Locale;

public class StringContainsCaseInsensitiveOperator implements Operator<String> {

    @Override
    public boolean test(String input, String value) {
        return input.toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT));
    }
}
