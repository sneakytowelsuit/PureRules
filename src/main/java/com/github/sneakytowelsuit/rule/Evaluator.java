package com.github.sneakytowelsuit.rule;

public sealed interface Evaluator<Input> permits RuleGroup, Rule {
}
