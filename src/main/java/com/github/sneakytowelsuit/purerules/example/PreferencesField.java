package com.github.sneakytowelsuit.purerules.example;

import com.github.sneakytowelsuit.purerules.conditions.Field;

import java.util.function.Function;


public class PreferencesField implements Field<Input, Preferences> {
    @Override
    public Function<Input, Preferences> getFieldValueFunction() {
        return Input::preferences;
    }
}
