package com.github.sneakytowelsuit;

import com.github.sneakytowelsuit.rule.Combinator;
import com.github.sneakytowelsuit.rule.Rule;
import com.github.sneakytowelsuit.rule.RuleGroup;
import com.github.sneakytowelsuit.sample.*;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    System.out.println("Starting PureRules...");
    InputUsernameField inputUsernameField = new InputUsernameField();
    StringEqualsOperator stringEqualsOperator = new StringEqualsOperator();

    // True
    Rule<Input, String> testRule1 = Rule.<Input, String>builder()
            .field(inputUsernameField)
            .operator(stringEqualsOperator)
            .value(new MyUsernameValue())
            .build();
    // False
    Rule<Input, String> testRule2 = Rule.<Input, String>builder()
            .field(inputUsernameField)
            .operator(stringEqualsOperator)
            .value(new AnotherUsernameValue())
            .build();
    // True
    Rule<Input, String> testRule3 = Rule.<Input, String>builder()
            .field(inputUsernameField)
            .operator(stringEqualsOperator)
            .value(new MyEmailValue())
            .build();
    RuleGroup<Input> testRuleGroup = RuleGroup.<Input>builder()
            .id("someId")
            .combinator(Combinator.OR)
            .isInverted(true)
            .conditions(List.of(testRule1, testRule2, testRule3))
            .build();
    Input input = new Input(MyUsernameValue.VALUE, MyUsernameValue.VALUE);
    boolean result = testRuleGroup.evaluate(input);
    System.out.println(result);
  }
}
