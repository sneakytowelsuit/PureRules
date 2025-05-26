package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Value;

public class AnotherUsernameValue implements Value<String> {
    public static final String VALUE = "AnotherUsernameValue";
    @Override
    public String getValue() {
        return VALUE;
    }
}
