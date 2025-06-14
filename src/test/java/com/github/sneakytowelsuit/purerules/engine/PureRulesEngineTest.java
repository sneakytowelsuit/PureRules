package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.TestUtils;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.example.Input;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PureRulesEngineTest {
    RuleGroup<String> ruleGroup = RuleGroup.<String>builder()
            .conditions(List.of(
                    RuleGroup.<String>builder()
                            .conditions(List.of(
                                    TestUtils.alwaysTrueRule(),
                                    TestUtils.alwaysFalseRule()
                            ))
                            .build(),
                    RuleGroup.<String>builder()
                            .conditions(List.of(
                                    TestUtils.alwaysTrueRule(),
                                    TestUtils.alwaysTrueRule(),
                                    TestUtils.alwaysTrueRule()
                            ))
                            .build()
            ))
            .build();

    @Test
    void testEngineInitialization() {
        PureRulesEngine<String> engine = PureRulesEngine.getDeterministicEngine(List.of(ruleGroup));
        assertNotNull(engine);
        assertEquals(EngineMode.DETERMINISTIC, engine.getEngineMode());
        assertNotNull(engine.getContext());
        assertNull(engine.getMinimumProbabilityThreshold());
    }

    @Test
    void testProbabilisticEngineInitialization() {
        PureRulesEngine<String> engine = PureRulesEngine.getProbablisticEngine(0.5f, List.of(ruleGroup));
        assertNotNull(engine);
        assertEquals(EngineMode.PROBABILISTIC, engine.getEngineMode());
        assertEquals(0.5f, engine.getMinimumProbabilityThreshold());
        assertNotNull(engine.getContext());
    }
}