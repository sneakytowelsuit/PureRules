package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;
import com.github.sneakytowelsuit.purerules.Operator;

import java.util.Locale;

public class StringContainsCaseInsensitiveOperator implements Operator<Input, String> {

    @Override
    public boolean test(Input input, Field<Input, String> field, String value) {
        return field.getFieldValueFunction().apply(input).toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT));
    }
}
