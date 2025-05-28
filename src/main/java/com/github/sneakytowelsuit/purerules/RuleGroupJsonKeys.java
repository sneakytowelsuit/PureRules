package com.github.sneakytowelsuit.purerules;

import lombok.Getter;

@Getter
public enum RuleGroupJsonKeys {
    FIELD("field"),
    OPERATOR("operator"),
    VALUE("value"),
    CONDITIONS("conditions"),
    INVERTED("inverted"),
    BIAS("bias"),
    COMBINATOR("combinator");
    private final String key;
    RuleGroupJsonKeys(String key){
        this.key = key;
    }
}
