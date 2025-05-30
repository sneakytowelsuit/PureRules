package com.github.sneakytowelsuit.purerules.example;

import com.github.sneakytowelsuit.purerules.conditions.Field;

import java.util.function.Function;

public class InputEmailField implements Field<Input, String> {
    @Override
    public Function<Input, String> getFieldValueFunction() {
        return Input::email;
    }
}
