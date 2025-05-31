package com.github.sneakytowelsuit.purerules.exceptions;

public class RuleGroupSerializationException extends RuntimeException {
    public RuleGroupSerializationException(String message) {
        super(message);
    }
    public RuleGroupSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
