package com.github.sneakytowelsuit.rule;

import com.github.sneakytowelsuit.sample.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleTest {
    private static final InputUsernameField inputUsernameField = new InputUsernameField();
    private static final MyUsernameValue myUsernameValue = new MyUsernameValue();
    private static final StringEqualsOperator stringEqualsOperator = new StringEqualsOperator();

    @Test
    void testTrue(){
        Rule<Input, String> testRule = Rule.<Input, String>builder()
                .field(inputUsernameField)
                .value(myUsernameValue)
                .operator(stringEqualsOperator)
                .build();
        Input testInput = new Input(MyUsernameValue.VALUE, MyEmailValue.VALUE, new Preferences(true));
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
        Input testInput = new Input(MyUsernameValue.VALUE + "extra stuff", MyEmailValue.VALUE,  new Preferences(true));
        boolean ruleResult = testRule.test(testInput);
        assertFalse(ruleResult);
    }
}