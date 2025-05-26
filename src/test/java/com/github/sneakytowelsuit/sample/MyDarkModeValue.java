package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Value;

public class MyDarkModeValue implements Value<Boolean> {
    public static final Boolean VALUE = true;

    @Override
    public Boolean getValue() {
        return VALUE;
    }
}
