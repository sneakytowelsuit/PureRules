package com.github.sneakytowelsuit.purerules.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EqualsOperatorTest {
    private final EqualsOperator<String> stringOperator = new EqualsOperator<>();
    private final EqualsOperator<Integer> intOperator = new EqualsOperator<>();

    @Test
    void testStringEquals() {
        assertTrue(stringOperator.test("abc", "abc"));
        assertFalse(stringOperator.test("abc", "def"));
        assertFalse(stringOperator.test("abc", null));
        assertFalse(stringOperator.test(null, "abc"));
        assertTrue(stringOperator.test(null, null));
    }

    @Test
    void testIntegerEquals() {
        assertTrue(intOperator.test(42, 42));
        assertFalse(intOperator.test(42, 43));
        assertFalse(intOperator.test(42, null));
        assertFalse(intOperator.test(null, 42));
        assertTrue(intOperator.test(null, null));
    }
}

