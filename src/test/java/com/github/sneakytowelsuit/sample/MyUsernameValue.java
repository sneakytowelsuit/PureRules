package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Value;

public class MyUsernameValue implements Value<String> {
    public static final String VALUE = "some username";
    @Override
    public String getValue() {
        return VALUE;
    }
}
