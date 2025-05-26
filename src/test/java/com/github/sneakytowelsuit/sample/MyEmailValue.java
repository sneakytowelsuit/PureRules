package com.github.sneakytowelsuit.sample;

import com.github.sneakytowelsuit.rule.Value;

public class MyEmailValue implements Value<String> {
    public static final String VALUE = "someemail";
    @Override
    public String getValue() {
        return VALUE;
    }
}
