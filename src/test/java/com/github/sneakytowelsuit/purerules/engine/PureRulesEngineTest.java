package com.github.sneakytowelsuit.purerules.engine;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class PureRulesEngineTest {

  private class Something {
    private final String name;

    public Something(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  private class SomethingNameField implements Field<Something, String> {
    @Override
    public Function<Something, String> getFieldValueFunction() {
      return Something::getName;
    }
  }

  private class StringEqualsCaseInsensitiveOperator implements Operator<String> {

    @Override
    public boolean test(String input, String s) {
      return input.toLowerCase(Locale.ROOT).equals(s.toLowerCase(Locale.ROOT));
    }
  }
  private class StringEqualsCaseSensitiveOperator implements Operator<String> {

    @Override
    public boolean test(String input, String s) {
      return input.equals(s);
    }
  }

  @Test
  void testEngineInitialization() {
    PureRulesEngine<Something, String> engine = PureRulesEngine.<Something, String>getDeterministicEngine(x -> x.getName(), List.of(
            RuleGroup.<Something>builder()
                    .conditions(List.of(
                            Rule.<Something, String>builder()
                                    .field(new SomethingNameField())
                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                    .value("test") // this one should pass
                                    .build(),
                            Rule.<Something, String>builder()
                                    .field(new SomethingNameField())
                                    .operator(new StringEqualsCaseSensitiveOperator())
                                    .value("test") // this one should fail
                                    .build()
                    ))
                    .combinator(Combinator.OR) // OR combinator means at least one condition must be true
                    .build()
    ));
    assertNotNull(engine);
    Something testObject = new Something("Test");
    Map<String, Boolean> results = engine.evaluate(testObject);
    results.forEach((key, value) -> assertTrue(value));
  }

  @Test
  void testProbabilisticEngineInitialization() {
  }
}
