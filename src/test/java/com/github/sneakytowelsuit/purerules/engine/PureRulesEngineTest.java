package com.github.sneakytowelsuit.purerules.engine;

import static org.junit.jupiter.api.Assertions.*;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import java.util.List;
import org.junit.jupiter.api.Test;

class PureRulesEngineTest {
  RuleGroup<String> ruleGroup =
      RuleGroup.<String>builder()
          .conditions(
              List.of(
                  RuleGroup.<String>builder()
                      .conditions(List.of(TestUtils.alwaysTrueRule(), TestUtils.alwaysFalseRule()))
                      .build(),
                  RuleGroup.<String>builder()
                      .conditions(
                          List.of(
                              TestUtils.alwaysTrueRule(),
                              TestUtils.alwaysTrueRule(),
                              TestUtils.alwaysTrueRule()))
                      .build()))
          .build();

  @Test
  void testEngineInitialization() {
    PureRulesEngine<String> engine = PureRulesEngine.getDeterministicEngine(List.of(ruleGroup));
    assertNotNull(engine);
  }

  @Test
  void testProbabilisticEngineInitialization() {
    PureRulesEngine<String> engine =
        PureRulesEngine.getProbabilisticEngine(0.5f, List.of(ruleGroup));
    assertNotNull(engine);
  }
}
