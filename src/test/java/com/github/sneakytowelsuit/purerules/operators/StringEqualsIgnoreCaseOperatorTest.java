package com.github.sneakytowelsuit.purerules.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StringEqualsIgnoreCaseOperatorTest {
    private final StringEqualsIgnoreCaseOperator operator = new StringEqualsIgnoreCaseOperator();

    @Test
    void testEqualsIgnoreCase() {
        assertTrue(operator.test("hello", "HELLO"));
        assertTrue(operator.test("Java", "java"));
        assertFalse(operator.test("Java", "Python"));
        assertFalse(operator.test(null, "test"));
        assertFalse(operator.test("test", null));
        assertFalse(operator.test(null, null));
    }
}

