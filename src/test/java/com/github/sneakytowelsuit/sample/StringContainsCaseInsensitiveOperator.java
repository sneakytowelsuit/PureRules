package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Field;
import com.github.sneakytowelsuit.rule.Operator;
import com.github.sneakytowelsuit.rule.Value;

import java.util.Locale;

public class StringContainsCaseInsensitiveOperator implements Operator<Input, String> {

    @Override
    public boolean test(Input input, Field<Input, String> field, Value<String> value) {
        return field.getFieldValueFunction().apply(input).toLowerCase(Locale.ROOT).contains(value.getValue().toLowerCase(Locale.ROOT));
    }
}
