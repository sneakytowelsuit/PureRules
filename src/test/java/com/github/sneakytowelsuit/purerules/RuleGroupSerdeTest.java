package com.github.sneakytowelsuit.purerules;

import com.github.sneakytowelsuit.purerules.conditions.Combinator;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.example.*;
import com.github.sneakytowelsuit.purerules.operators.EqualsOperator;
import com.github.sneakytowelsuit.purerules.serialization.RuleGroupSerde;
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
    private static final EqualsOperator<String> stringEqualsOperator = new EqualsOperator<>();
    private static final EqualsOperator<Boolean> booleanEqualsOperator = new EqualsOperator<>();
    @Test
    void there_and_back_again(){
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
                                .build(),
                        Rule.<Input, Preferences>builder()
                                .field(new PreferencesField())
                                .operator(new PreferencesEqualsOperator())
                                .value(new Preferences(true, "someone"))
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