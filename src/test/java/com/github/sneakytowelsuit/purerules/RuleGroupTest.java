package com.github.sneakytowelsuit.purerules;

import com.github.sneakytowelsuit.sample.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleGroupTest {
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
    private static final StringContainsCaseInsensitiveOperator stringContainsCaseInsensitiveOperator = new StringContainsCaseInsensitiveOperator();
    private static final BooleanEqualsOperator booleanEqualsOperator = new BooleanEqualsOperator();

    @Test
    public void noConditions_defaultPessimistic(){
       Input input = new Input("", "", new Preferences(true, ""));
       RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
               .build();
       boolean result = ruleGroup.evaluate(input);
       assertFalse(result);
    }

    @Test
    public void noConditions_defaultPessimisticInverted(){
        Input input = new Input("", "", new Preferences(true, ""));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .isInverted(true)
               .build();
        boolean result = ruleGroup.evaluate(input);
        assertTrue(result);
    }

    @Test
    public void noConditions_optimisticBias() {
        Input input = new Input("", "", new Preferences(true, ""));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .bias(Bias.INCLUSIVE)
                .build();
        boolean result = ruleGroup.evaluate(input);
        assertTrue(result);
    }

    @Test
    public void noConditions_optimisticBiasInverted() {
        Input input = new Input("", "", new Preferences(true, ""));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .bias(Bias.INCLUSIVE)
                .isInverted(true)
                .build();
        boolean result = ruleGroup.evaluate(input);
        assertFalse(result);
    }

    @Test
    public void singleCondition_passesRule() {
        Input input = new Input(myUsernameValue, "", new Preferences(true, ""));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .conditions(List.of(
                        Rule.<Input, String>builder()
                                .field(inputUsernameField)
                                .operator(stringEqualsOperator)
                                .value(myUsernameValue)
                                .build()
                ))
                .build();
        assertTrue(ruleGroup.evaluate(input));
    }

    @Test
    public void singleCondition_passesRuleInverted() {
        Input input = new Input(myUsernameValue + "something to fail", "", new Preferences(true, ""));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .conditions(List.of(
                        Rule.<Input, String>builder()
                                .field(inputUsernameField)
                                .operator(stringEqualsOperator)
                                .value(myUsernameValue)
                                .build()
                ))
                .isInverted(true)
                .build();
        assertTrue(ruleGroup.evaluate(input));
    }

    @Test
    public void singleCondition_deepInInputPassesRule() {
        Input input = new Input("", "", new Preferences(myDarkModeValue, ""));
        Rule<Input, Boolean> rule = Rule.<Input, Boolean>builder()
                .value(myDarkModeValue)
                .operator(booleanEqualsOperator)
                .field(inputDarkModePreferenceField)
                .build();
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
                .conditions(List.of(rule))
                .build();
        assertTrue(ruleGroup.evaluate(input));
    }

    @Test
    public void multipleConditions_passesRuleGroup() {
        Input input = new Input(myUsernameValue, myEmailValue, new Preferences(myDarkModeValue, ""));
        RuleGroup<Input> ruleGroup = RuleGroup.<Input>builder()
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
                .build();
        assertTrue(ruleGroup.evaluate(input));
    }

    @Test
    public void multipleConditions_nestedConditionPassesRuleGroup() {
        Input input = new Input(myUsernameValue, myEmailValue, new Preferences(myDarkModeValue, ""));
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
        assertTrue(ruleGroup.evaluate(input));
    }

    @Test
    public void multipleConditions_nestedConditionFailsRuleGroup() {
        Input input = new Input(myUsernameValue + "something to fail it", myEmailValue, new Preferences(myDarkModeValue, ""));
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
        assertFalse(ruleGroup.evaluate(input));
    }

    @Test
    public void multipleConditions_nestConditionsOrCombinatorPassesRuleGroup() {
        Input input = new Input(myUsernameValue + "something to fail it", myEmailValue, new Preferences(myDarkModeValue, ""));
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
        assertTrue(ruleGroup.evaluate(input));
    }

    @Test
    public void multipleConditions_nestConditionsOrCombinatorFailsRuleGroupWithInversion() {
        Input input = new Input(myUsernameValue + "something to fail it", myEmailValue, new Preferences(myDarkModeValue, ""));
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
        assertFalse(ruleGroup.evaluate(input));
    }
}