package com.github.sneakytowelsuit.purerules.example;

import com.github.sneakytowelsuit.purerules.conditions.Field;

import java.util.function.Function;

public class InputDarkModePreferenceField implements Field<Input, Boolean> {
    @Override
    public Function<Input, Boolean> getFieldValueFunction() {
        return input -> input.preferences().getDarkMode();
    }
}
