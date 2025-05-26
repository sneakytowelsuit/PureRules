package com.github.sneakytowelsuit.rule;

import lombok.Getter;

@Getter
public enum Bias {
    INCLUSIVE(true),
    EXCLUSIVE(false);
    private final boolean biasResult;
    Bias(boolean biasResult){
        this.biasResult = biasResult;
    }
}
