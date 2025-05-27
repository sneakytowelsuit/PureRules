package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Field;
import com.github.sneakytowelsuit.rule.Operator;

import java.util.Locale;

public class StringContainsCaseInsensitiveOperator implements Operator<Input, String> {

    @Override
    public boolean test(Input input, Field<Input, String> field, String value) {
        return field.getFieldValueFunction().apply(input).toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT));
    }
}
