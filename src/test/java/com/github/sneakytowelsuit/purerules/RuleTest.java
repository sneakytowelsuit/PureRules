package com.github.sneakytowelsuit.purerules;

import com.github.sneakytowelsuit.sample.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest {
    private static final InputUsernameField inputUsernameField = new InputUsernameField();
    private static final String myUsernameValue = "some username";
    private static final String myEmailValue = "some email";
    private static final StringEqualsOperator stringEqualsOperator = new StringEqualsOperator();

    @Test
    void testTrue(){
        Rule<Input, String> testRule = Rule.<Input, String>builder()
                .field(inputUsernameField)
                .value(myUsernameValue)
                .operator(stringEqualsOperator)
                .build();
        Input testInput = new Input(myUsernameValue, myEmailValue, new Preferences(true));
        boolean ruleResult = testRule.test(testInput);
        assertTrue(ruleResult);
    }
    @Test
    void testFalse(){
        Rule<Input, String> testRule = Rule.<Input, String>builder()
                .field(inputUsernameField)
                .value(myUsernameValue)
                .operator(stringEqualsOperator)
                .build();
        Input testInput = new Input(myUsernameValue+ "extra stuff", myEmailValue,  new Preferences(true));
        boolean ruleResult = testRule.test(testInput);
        assertFalse(ruleResult);
    }
}