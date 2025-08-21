// spotless:off
/**
 * Context management for evaluation state tracking and field value caching.
 *
 * <p>This package provides the context management infrastructure that enables the PureRules
 * engine to efficiently cache field values and track evaluation state across rule processing.
 * The context system improves performance by avoiding repeated field value extraction and 
 * provides detailed evaluation information for debugging and analysis.
 *
 * <p><strong>Key Components:</strong>
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.EngineContextService} - 
 *       Main context management service</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.field} - 
 *       Field value caching infrastructure</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.condition} - 
 *       Condition evaluation result tracking</li>
 * </ul>
 *
 * <p><strong>Architecture Overview:</strong>
 * The context system uses a two-tier caching approach:
 * <ul>
 *   <li><strong>Field Context:</strong> Caches extracted values from input objects to avoid
 *       repeated field extraction calls</li>
 *   <li><strong>Condition Context:</strong> Tracks intermediate evaluation results and metadata
 *       for debugging and analysis</li>
 * </ul>
 *
 * <p><strong>Performance Benefits:</strong>
 * <pre>{@code
 * // Without context caching, this would extract person.getAge() twice:
 * RuleGroup<Person> ageChecks = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .conditions(Arrays.asList(
 *         Rule.<Person, Integer>builder()
 *             .field(new AgeField())  // person.getAge() - call #1
 *             .operator(new GreaterThanOperator<>())
 *             .value(18)
 *             .build(),
 *         Rule.<Person, Integer>builder()
 *             .field(new AgeField())  // person.getAge() - call #2 (cached!)
 *             .operator(new LessThanOperator<>()) 
 *             .value(65)
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 *
 * <p><strong>Context Lifecycle:</strong>
 * Context information is automatically managed during evaluation:
 * <ol>
 *   <li>Context is created when evaluation begins</li>
 *   <li>Field values are cached on first extraction</li>
 *   <li>Condition results are stored as evaluation progresses</li>
 *   <li>Context is flushed after evaluation completes to prevent memory leaks</li>
 * </ol>
 *
 * <p><strong>Thread Safety:</strong>
 * All context components are designed to be thread-safe, using concurrent data structures
 * where necessary. Multiple threads can safely evaluate different inputs simultaneously.
 *
 * <p><strong>Memory Management:</strong>
 * <pre>{@code
 * // Context is automatically flushed after evaluation
 * Map<String, Boolean> results = engine.evaluate(person);
 * // Field and condition context for 'person' is cleared here
 *
 * // Manual context management (advanced usage)
 * EngineContextService<Person, String> context = engine.getEngineContextService();
 * context.flush(specificPerson);    // Clear context for specific input
 * context.flushAll();               // Clear all cached context
 * }</pre>
 *
 * <p><strong>Debugging Support:</strong>
 * The context system provides detailed information about evaluation flow, making it easier
 * to understand why rules passed or failed and to optimize rule performance.
 *
 * <p><strong>Integration with Evaluation Services:</strong>
 * Context management is tightly integrated with both deterministic and probabilistic
 * evaluation services, providing consistent caching and tracking behavior regardless
 * of engine mode.
 *
 * @see com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 * @see com.github.sneakytowelsuit.purerules.context.field
 * @see com.github.sneakytowelsuit.purerules.context.condition
 */
// spotless:on
package com.github.sneakytowelsuit.purerules.context;
