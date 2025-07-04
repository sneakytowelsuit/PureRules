package com.github.sneakytowelsuit.purerules.testutils;

import com.github.sneakytowelsuit.purerules.conditions.Field;
import com.github.sneakytowelsuit.purerules.conditions.Operator;
import java.util.Locale;
import java.util.function.Function;

public class TestHelpers {
    public static class Something {
        private final String name;
        private final int id;

        public Something(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public static class SomethingNameField implements Field<Something, String> {
        @Override
        public Function<Something, String> getFieldValueFunction() {
            return Something::getName;
        }
    }

    public static class StringEqualsCaseInsensitiveOperator implements Operator<String> {
        @Override
        public boolean test(String input, String s) {
            return input != null && s != null && input.toLowerCase(Locale.ROOT).equals(s.toLowerCase(Locale.ROOT));
        }
    }
}

