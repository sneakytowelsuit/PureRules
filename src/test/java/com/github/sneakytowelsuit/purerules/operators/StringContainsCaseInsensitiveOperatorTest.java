package com.github.sneakytowelsuit.purerules.operators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StringContainsCaseInsensitiveOperatorTest {
    private final StringContainsCaseInsensitiveOperator operator = new StringContainsCaseInsensitiveOperator();

    @Test
    void testContainsCaseInsensitive() {
        assertTrue(operator.test("Hello World", "hello"));
        assertTrue(operator.test("Hello World", "WORLD"));
        assertTrue(operator.test("Java", "va"));
        assertFalse(operator.test("Java", "python"));
        assertFalse(operator.test(null, "test"));
        assertFalse(operator.test("test", null));
        assertFalse(operator.test(null, null));
    }
}

