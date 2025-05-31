package com.github.sneakytowelsuit.purerules.serialization;

import lombok.Getter;

@Getter
public enum RuleGroupJsonKeys {
    FIELD("field"),
    OPERATOR("operator"),
    VALUE("value"),
    VALUE_CLASS("class"),
    CONDITIONS("conditions"),
    VALUE_VALUE("value"),
    INVERTED("inverted"),
    BIAS("bias"),
    COMBINATOR("combinator");
    private final String key;
    RuleGroupJsonKeys(String key){
        this.key = key;
    }
}
