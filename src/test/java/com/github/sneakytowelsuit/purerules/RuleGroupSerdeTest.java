package com.github.sneakytowelsuit.purerules;

import com.github.sneakytowelsuit.sample.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleGroupSerdeTest {
    // Fields
    private static final InputUsernameField inputUsernameField = new InputUsernameField();
    private static final InputEmailField inputEmailField = new InputEmailField();
    private static final InputDarkModePreferenceField inputDarkModePreferenceField = new InputDarkModePreferenceField();

    // Values
    private static final String myUsernameValue = "some username";
    private static final String myEmailValue = "some email";
    private static final Boolean myDarkModeValue = true;

    // Operators
    private static final StringEqualsOperator stringEqualsOperator = new StringEqualsOperator();
    private static final BooleanEqualsOperator booleanEqualsOperator = new BooleanEqualsOperator();
    @Test
    void playground(){
        RuleGroup<Input> nestedRuleGroup = RuleGroup.<Input>builder()
                .conditions(List.of(
                        Rule.<Input, String>builder()
                                .field(inputUsernameField)
                                .operator(stringEqualsOperator)
                                .value(myUsernameValue)
                                .build(),
                        Rule.<Input, String>builder()
                                .field(inputEmailField)
                                .operator(stringEqualsOperator)
                                .value(myEmailValue)
                                .build()
                ))
                .combinator(Combinator.OR)
                .isInverted(true)
                .build();
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .conditions(List.of(
                        Rule.<Input, Boolean>builder()
                                .operator(booleanEqualsOperator)
                                .field(inputDarkModePreferenceField)
                                .value(myDarkModeValue)
                                .build(),
                        nestedRuleGroup
                ))
                .build();
        RuleGroupSerde<Input> ruleGroupSerde = new RuleGroupSerde<Input>();
        String serializedRuleGroup = ruleGroupSerde.serialize(ruleGroup);
        RuleGroup<Input>  deserializedRuleGroup = ruleGroupSerde.deserialize(serializedRuleGroup);
        assertFalse(serializedRuleGroup.isBlank());
        assertEquals(ruleGroup.getBias(), deserializedRuleGroup.getBias());
        assertEquals(ruleGroup.getCombinator(), deserializedRuleGroup.getCombinator());
        assertEquals(ruleGroup.isInverted(), deserializedRuleGroup.isInverted());
    }
}