package com.github.sneakytowelsuit.purerules.exceptions;

public class RuleGroupDeserializationException extends RuntimeException {
    public RuleGroupDeserializationException(String message) {
        super(message);
    }
    public RuleGroupDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
