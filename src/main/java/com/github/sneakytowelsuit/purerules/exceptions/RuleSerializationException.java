package com.github.sneakytowelsuit.purerules.exceptions;

public class RuleSerializationException extends RuntimeException {
    public RuleSerializationException(String message) {
      super(message);
    }
    public RuleSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
