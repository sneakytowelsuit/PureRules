package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;

import java.util.function.Function;

public class InputDarkModePreferenceField implements Field<Input, Boolean> {
    @Override
    public Function<Input, Boolean> getFieldValueFunction() {
        return input -> input.preferences().darkMode();
    }
}
