package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.purerules.Field;

import java.util.function.Function;


public class PreferencesField implements Field<Input, Preferences> {
    @Override
    public Function<Input, Preferences> getFieldValueFunction() {
        return Input::preferences;
    }
}
