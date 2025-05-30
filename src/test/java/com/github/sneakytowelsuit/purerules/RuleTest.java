package com.github.sneakytowelsuit.purerules;

import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.example.Input;
import com.github.sneakytowelsuit.purerules.example.InputUsernameField;
import com.github.sneakytowelsuit.purerules.example.Preferences;
import com.github.sneakytowelsuit.purerules.operators.EqualsOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest {
    private static final InputUsernameField inputUsernameField = new InputUsernameField();
    private static final String myUsernameValue = "some username";
    private static final String myEmailValue = "some email";
    private static final EqualsOperator<String> stringEqualsOperator = new EqualsOperator<>();

    @Test
    void testTrue(){
        Rule<Input, String> testRule = Rule.<Input, String>builder()
                .field(inputUsernameField)
                .value(myUsernameValue)
                .operator(stringEqualsOperator)
                .build();
        Input testInput = new Input(myUsernameValue, myEmailValue, new Preferences(true, ""));
        boolean ruleResult = testRule.evaluate(testInput);
        assertTrue(ruleResult);
    }
    @Test
    void testFalse(){
        Rule<Input, String> testRule = Rule.<Input, String>builder()
                .field(inputUsernameField)
                .value(myUsernameValue)
                .operator(stringEqualsOperator)
                .build();
        Input testInput = new Input(myUsernameValue+ "extra stuff", myEmailValue,  new Preferences(true, ""));
        boolean ruleResult = testRule.evaluate(testInput);
        assertFalse(ruleResult);
    }
}