package com.github.sneakytowelsuit.rule;

import com.github.sneakytowelsuit.sample.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleGroupTest {
    // Fields
    private static final InputUsernameField inputUsernameField = new InputUsernameField();
    private static final InputEmailField inputEmailField = new InputEmailField();

    // Values
    private static final MyUsernameValue myUsernameValue = new MyUsernameValue();
    private static final MyEmailValue myEmailValue = new MyEmailValue();

    // Operators
    private static final StringEqualsOperator stringEqualsOperator = new StringEqualsOperator();
    private static final StringContainsCaseInsensitiveOperator stringContainsCaseInsensitiveOperator = new StringContainsCaseInsensitiveOperator();

    @Test
    public void noConditions_defaultPessimistic(){
       Input input = new Input("", "", new Preferences(true));
       RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
               .build();
       boolean result = ruleGroup.evaluate(input);
       assertFalse(result);
    }

    @Test
    public void noConditions_defaultPessimisticInverted(){
        Input input = new Input("", "", new Preferences(true));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .isInverted(true)
               .build();
        boolean result = ruleGroup.evaluate(input);
        assertTrue(result);
    }

    @Test
    public void noConditions_optimisticBias() {
        Input input = new Input("", "", new Preferences(true));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .bias(Bias.OPTIMISTIC)
                .build();
        boolean result = ruleGroup.evaluate(input);
        assertTrue(result);
    }
    @Test
    public void noConditions_optimisticBiasInverted() {
        Input input = new Input("", "", new Preferences(true));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .bias(Bias.OPTIMISTIC)
                .isInverted(true)
                .build();
        boolean result = ruleGroup.evaluate(input);
        assertFalse(result);
    }
}