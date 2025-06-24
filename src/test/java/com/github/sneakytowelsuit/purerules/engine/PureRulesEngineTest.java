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
    PureRulesEngine<Something, Integer> engine = PureRulesEngine.<Something, Integer>getDeterministicEngine(Something::getId, List.of(
            RuleGroup.<Something>builder()
                    .conditions(List.of(
                            Rule.<Something, String>builder()
                                    .field(new SomethingNameField())
                                    .operator(new StringEqualsCaseSensitiveOperator())
                                    .value("test") // this one should fail
                                    .build(),
                            RuleGroup.<Something>builder()
                                    .conditions(List.of(
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build(),
                                            RuleGroup.<Something>builder()
                                                    .conditions(List.of(
                                                            Rule.<Something, String>builder()
                                                                    .field(new SomethingNameField())
                                                                    .value("test")
                                                                    .operator(new StringEqualsCaseSensitiveOperator())
                                                                    .build(),
                                                            Rule.<Something, String>builder()
                                                                    .field(new SomethingNameField())
                                                                    .value("test")
                                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                                    .build()
                                                    ))
                                                    .combinator(Combinator.OR)
                                                    .build(),
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build(),
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build(),
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build()
                                    ))
                                    .combinator(Combinator.AND)
                                    .build()
                    ))
                    .combinator(Combinator.OR)
                    .build()
    ));
    assertNotNull(engine);
    Something testObject = new Something(1234, "Test");
    Map<String, Boolean> results = engine.evaluate(testObject);
    results.forEach((key, value) -> assertTrue(value));
  }

  @Test
  void testProbabilisticEngineInitialization() {
    PureRulesEngine<Something, Integer> engine = PureRulesEngine.<Something, Integer>getProbablisticEngine(Something::getId, 0.75f, List.of(
            RuleGroup.<Something>builder()
                    .conditions(List.of(
                            Rule.<Something, String>builder()
                                    .field(new SomethingNameField())
                                    .operator(new StringEqualsCaseSensitiveOperator())
                                    .value("test") // this one should fail
                                    .build(),
                            RuleGroup.<Something>builder()
                                    .conditions(List.of(
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build(),
                                            RuleGroup.<Something>builder()
                                                    .conditions(List.of(
                                                            Rule.<Something, String>builder()
                                                                    .field(new SomethingNameField())
                                                                    .value("test")
                                                                    .operator(new StringEqualsCaseSensitiveOperator())
                                                                    .build(),
                                                            Rule.<Something, String>builder()
                                                                    .field(new SomethingNameField())
                                                                    .value("test")
                                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                                    .build()
                                                    ))
                                                    .weight(2)
                                                    .combinator(Combinator.OR)
                                                    .build(),
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build(),
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build(),
                                            Rule.<Something, String>builder()
                                                    .field(new SomethingNameField())
                                                    .value("test")
                                                    .operator(new StringEqualsCaseInsensitiveOperator())
                                                    .build()
                                    ))
                                    .combinator(Combinator.AND)
                                    .weight(1000)
                                    .build()
                    ))
                    .combinator(Combinator.OR)
                    .build()
    ));
    assertNotNull(engine);
    Something testObject = new Something(1234, "Test");
    Map<String, Boolean> results = engine.evaluate(testObject);
    results.forEach((key, value) -> assertTrue(value));
  }
}
