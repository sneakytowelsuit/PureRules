package com.github.sneakytowelsuit.purerules.evaluation;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeterministicEvaluationService<TInput> implements EvaluationService<TInput> {
    // List of conditions to evaluate, defaulting to an empty list.
    private List<Condition<TInput>> conditions = List.of();
    public DeterministicEvaluationService(
            final List<Condition<TInput>> conditions
    ) {
        this.conditions = conditions;
    }

    @Override
    public Map<String, Boolean> evaluate(TInput input) {
        return conditions.stream()
                .collect(Collectors.toMap(
                        Condition::getId,
                        condition -> evaluationConditions(input, condition)
                ));
    }

    private boolean evaluationConditions(
            TInput input,
            Condition<TInput> condition
    ) {
        return switch (condition) {
            case Rule<TInput, ?> rule -> evaluateRule(input, rule);
            case RuleGroup<TInput> ruleGroup -> evaluateRuleGroup(input, ruleGroup);
        };
    }

    private boolean evaluateRuleGroup(
            TInput input,
            RuleGroup<TInput> ruleGroup
    ) {
        if (ruleGroup.getConditions().isEmpty()) {
            return evaluateEmptyRuleGroup(ruleGroup);
        }
        // Sort the conditions by type
        List<Rule<TInput, ?>> rules = new ArrayList<>();
        List<RuleGroup<TInput>> ruleGroups = new ArrayList<>();
        for (Condition<TInput> condition: ruleGroup.getConditions()) {
            switch(condition) {
                case Rule<TInput, ?> rule -> rules.add(rule);
                case RuleGroup<TInput> ruleGroupCondition -> ruleGroups.add(ruleGroupCondition);
            }
        }
        // Evaluate the rules
        return switch(ruleGroup.getCombinator()) {
            case AND -> rules.stream().allMatch(rule -> evaluateRule(input, rule))
                    && ruleGroups.stream().allMatch(ruleGroupCondition -> evaluateRuleGroup(input, ruleGroupCondition));
            case OR -> rules.stream().anyMatch(rule -> evaluateRule(input, rule))
                    || ruleGroups.stream().anyMatch(ruleGroupCondition -> evaluateRuleGroup(input, ruleGroupCondition));
        } ^ ruleGroup.isInverted();
    }

    private boolean evaluateEmptyRuleGroup(
            RuleGroup<TInput> ruleGroup
    ) {
        return ruleGroup.getBias().isBiasResult() ^ ruleGroup.isInverted();
    }

    private boolean evaluateRule(
            TInput input,
            Rule<TInput, ?> rule
    ) {
        if (rule == null) {
            return false;
        }
        return rule.evaluate(input);
    }
}
