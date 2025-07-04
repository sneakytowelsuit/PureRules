package com.github.sneakytowelsuit.purerules.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotEqualsOperatorTest {
    private final NotEqualsOperator<String> stringOperator = new NotEqualsOperator<>();
    private final NotEqualsOperator<Integer> intOperator = new NotEqualsOperator<>();

    @Test
    void testStringNotEquals() {
        assertFalse(stringOperator.test("abc", "abc"));
        assertTrue(stringOperator.test("abc", "def"));
        assertTrue(stringOperator.test("abc", null));
        assertTrue(stringOperator.test(null, "abc"));
        assertFalse(stringOperator.test(null, null));
    }

    @Test
    void testIntegerNotEquals() {
        assertFalse(intOperator.test(42, 42));
        assertTrue(intOperator.test(42, 43));
        assertTrue(intOperator.test(42, null));
        assertTrue(intOperator.test(null, 42));
        assertFalse(intOperator.test(null, null));
    }
}

