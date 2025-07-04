package com.github.sneakytowelsuit.purerules.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LessThanOperatorTest {
    private final LessThanOperator<Integer> intOperator = new LessThanOperator<>();
    private final LessThanOperator<String> stringOperator = new LessThanOperator<>();

    @Test
    void testIntegerLessThan() {
        assertTrue(intOperator.test(3, 5));
        assertFalse(intOperator.test(5, 3));
        assertFalse(intOperator.test(5, 5));
        assertFalse(intOperator.test(null, 1));
        assertFalse(intOperator.test(1, null));
    }

    @Test
    void testStringLessThan() {
        assertTrue(stringOperator.test("a", "b"));
        assertFalse(stringOperator.test("b", "a"));
        assertFalse(stringOperator.test("a", "a"));
    }
}

