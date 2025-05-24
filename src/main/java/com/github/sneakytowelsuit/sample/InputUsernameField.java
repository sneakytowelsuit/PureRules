package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Field;

import java.util.function.Function;

public class InputUsernameField implements Field<Input, String> {
    @Override
    public Function<Input, String> getFieldValueFunction() {
        return Input::username;
    }
}
