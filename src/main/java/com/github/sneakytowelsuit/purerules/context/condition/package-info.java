/**
 * Condition evaluation result tracking for debugging and analysis.
 *
 * <p>This package provides infrastructure for tracking condition evaluation results and metadata
 * during rule processing. It enables detailed analysis of evaluation flow, debugging support,
 * and comprehensive result tracking for both individual rules and rule groups.
 *
 * <p><strong>Core Components:</strong>
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.condition.ConditionContext} - 
 *       Main context storage for condition evaluation results</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.condition.ConditionContextKey} - 
 *       Unique key identifying condition evaluations</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.condition.ConditionContextValue} - 
 *       Base interface for evaluation result values</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.condition.RuleContextValue} - 
 *       Specific context for individual rule evaluations</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.condition.RuleGroupContextValue} - 
 *       Specific context for rule group evaluations</li>
 * </ul>
 *
 * <p><strong>Context Information Tracked:</strong>
 * The condition context system captures comprehensive evaluation metadata:
 * <ul>
 *   <li><strong>Evaluation Results:</strong> Boolean outcomes for each condition</li>
 *   <li><strong>Field Values:</strong> Actual values extracted during evaluation</li>
 *   <li><strong>Operator Results:</strong> Intermediate comparison results</li>
 *   <li><strong>Timing Information:</strong> Evaluation performance metrics</li>
 *   <li><strong>Error Information:</strong> Details about evaluation failures</li>
 * </ul>
 *
 * <p><strong>Rule Context Example:</strong>
 * <pre>{@code
 * // For this rule evaluation:
 * Rule<Person, Integer> ageRule = Rule.<Person, Integer>builder()
 *     .field(new AgeField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(18)
 *     .build();
 *
 * Person person = new Person("John", 25);
 * 
 * // The RuleContextValue captures:
 * // - fieldValue: 25 (extracted from person.getAge())
 * // - operatorResult: true (25 > 18)
 * // - finalResult: true
 * // - evaluationTime: ~0.1ms
 * }</pre>
 *
 * <p><strong>Rule Group Context Example:</strong>
 * <pre>{@code
 * // For this rule group evaluation:
 * RuleGroup<Person> eligibility = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .conditions(Arrays.asList(ageRule, citizenshipRule))
 *     .build();
 *
 * // The RuleGroupContextValue captures:
 * // - individualResults: [ageRule: true, citizenshipRule: false]
 * // - combinatorResult: false (true AND false = false)
 * // - finalResult: false (not inverted)
 * // - childContexts: [context for ageRule, context for citizenshipRule]
 * }</pre>
 *
 * <p><strong>Debugging Support:</strong>
 * Context information enables detailed debugging of rule evaluation:
 * <pre>{@code
 * // After evaluation, inspect context for debugging
 * EngineContextService<Person, String> contextService = engine.getEngineContextService();
 * ConditionContext<String> conditionContext = contextService.getConditionEvaluationContext();
 *
 * // Find specific rule evaluation
 * ConditionContextKey<String> key = new ConditionContextKey<>(person.getId(), ageRule);
 * RuleContextValue ruleContext = (RuleContextValue) conditionContext
 *     .getConditionContextMap().get(key);
 *
 * if (ruleContext != null) {
 *     System.out.println("Field value: " + ruleContext.getFieldValue());
 *     System.out.println("Comparison result: " + ruleContext.getOperatorResult());
 *     System.out.println("Final result: " + ruleContext.getFinalResult());
 * }
 * }</pre>
 *
 * <p><strong>Performance Analysis:</strong>
 * Context data can be used to identify performance bottlenecks and optimize rule evaluation:
 * <ul>
 *   <li>Track which fields are most expensive to extract</li>
 *   <li>Identify rules that consistently fail early</li>
 *   <li>Measure evaluation time for complex rule groups</li>
 *   <li>Analyze cache hit rates for field value extraction</li>
 * </ul>
 *
 * <p><strong>Memory Management:</strong>
 * Condition context information is automatically cleaned up after evaluation to prevent
 * memory leaks. Context can also be manually managed for advanced use cases:
 * <pre>{@code
 * // Automatic cleanup
 * Map<String, Boolean> results = engine.evaluate(person);
 * // Condition context is automatically flushed
 *
 * // Manual context management
 * contextService.flush(person);  // Clear context for specific input
 * contextService.flushAll();     // Clear all context information
 * }</pre>
 *
 * <p><strong>Thread Safety:</strong>
 * All condition context components are thread-safe and can be safely accessed from multiple
 * threads during concurrent evaluation operations.
 *
 * @see com.github.sneakytowelsuit.purerules.context.EngineContextService
 * @see com.github.sneakytowelsuit.purerules.conditions.Condition
 * @see com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService
 */
package com.github.sneakytowelsuit.purerules.context.condition;