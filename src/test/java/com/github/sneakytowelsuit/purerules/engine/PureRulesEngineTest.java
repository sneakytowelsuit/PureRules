package com.github.sneakytowelsuit.purerules.engine;

import static org.junit.jupiter.api.Assertions.*;

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
    PureRulesEngine<Something, Integer> engine =
        PureRulesEngine.<Something, Integer>getDeterministicEngine(
            Something::getId,
            List.of(
                RuleGroup.<Something>builder()
                    .conditions(
                        List.of(
                            Rule.<Something, String>builder()
                                .field(new SomethingNameField())
                                .operator(new StringEqualsCaseSensitiveOperator())
                                .value("test") // this one should fail
                                .build(),
                            RuleGroup.<Something>builder()
                                .conditions(
                                    List.of(
                                        Rule.<Something, String>builder()
                                            .field(new SomethingNameField())
                                            .value("test")
                                            .operator(new StringEqualsCaseInsensitiveOperator())
                                            .build(),
                                        RuleGroup.<Something>builder()
                                            .conditions(
                                                List.of(
                                                    Rule.<Something, String>builder()
                                                        .field(new SomethingNameField())
                                                        .value("test")
                                                        .operator(
                                                            new StringEqualsCaseSensitiveOperator())
                                                        .build(),
                                                    Rule.<Something, String>builder()
                                                        .field(new SomethingNameField())
                                                        .value("test")
                                                        .operator(
                                                            new StringEqualsCaseInsensitiveOperator())
                                                        .build()))
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
                                            .build()))
                                .combinator(Combinator.AND)
                                .build()))
                    .combinator(Combinator.OR)
                    .build()));
    assertNotNull(engine);
    Something testObject = new Something(1234, "Test");
    Map<String, Boolean> results = engine.evaluate(testObject);
    results.forEach((key, value) -> assertTrue(value));
  }

  /**
   * This test demonstrates a passing scenario for probabilistic evaluation using a RuleGroup tree
   * with a depth of at least 2. The tree structure is as follows:
   * <pre>
   * Root RuleGroup (AND)
   * ├── Rule: name equals "Test" (case-insensitive, weight 3)
   * └── Nested RuleGroup (OR, weight 7)
   *     ├── Rule: name equals "Test" (case-sensitive, weight 4)
   *     └── Rule: name equals "fail" (case-insensitive, weight 3)
   * </pre>
   * The minimum probability threshold is set to 0.7. For input "Test", both the root rule and
   * the first nested rule will pass, resulting in a score:
   * <ul>
   *     <li>Root Rule: 3 (case-insensitive pass)</li>
   *     <li>Nested RuleGroup: 4 (case-sensitive pass) + 0 (case-insensitive fail)</li>
   *     <li>Total: 3 (root) + 4 (nested) = 7</li>
   *     <li>Weights sum: 10 (3 + 4 + 3)</li>
   *     <li>Score: 7 / 10 = 0.7</li>
   * </ul>
   */
  @Test
  void testProbabilisticEvaluationWithNestedRuleGroups() {
    final String NAME = "Test";
    final float MIN_PROBABILITY = 0.7f;

    // Build a RuleGroup tree of depth 2
    RuleGroup<Something> nestedGroup =
        RuleGroup.<Something>builder()
            .conditions(
                List.of(
                    Rule.<Something, String>builder()
                        .field(new SomethingNameField())
                        .operator(new StringEqualsCaseSensitiveOperator())
                        .value(NAME)
                        .weight(4)
                        .build(),
                    Rule.<Something, String>builder()
                        .field(new SomethingNameField())
                        .operator(new StringEqualsCaseInsensitiveOperator())
                        .value("fail") // This will fail, so only 4/7 from nested group
                        .weight(3)
                        .build()))
            .combinator(Combinator.OR)
            .build();

    RuleGroup<Something> rootGroup =
        RuleGroup.<Something>builder()
            .conditions(
                List.of(
                    Rule.<Something, String>builder()
                        .field(new SomethingNameField())
                        .operator(new StringEqualsCaseInsensitiveOperator())
                        .value(NAME)
                        .weight(3)
                        .build(),
                    nestedGroup))
            .combinator(Combinator.AND)
            .build();

    PureRulesEngine<Something, Integer> engine =
        PureRulesEngine.getProbablisticEngine(
            Something::getId, MIN_PROBABILITY, List.of(rootGroup));

    Something testObject = new Something(1, "Test");
    Map<String, Boolean> results = engine.evaluate(testObject);

    // All results should be true, as the minimum probability is met (7/10 = 0.7)
    results.forEach(
        (key, value) ->
            assertTrue(value, "RuleGroup " + key + " should pass probabilistic evaluation"));
  }
}
