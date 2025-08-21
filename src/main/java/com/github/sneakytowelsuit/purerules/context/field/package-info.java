// spotless:off
/**
 * Field value caching infrastructure for performance optimization.
 *
 * <p>This package contains the components responsible for caching field values extracted from
 * input objects during rule evaluation. Field caching prevents redundant field extraction calls
 * when multiple rules reference the same field, significantly improving evaluation performance
 * for complex rule sets.
 *
 * <p><strong>Core Components:</strong>
 * <ul>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.field.FieldContext} - 
 *       Thread-safe cache for field values</li>
 *   <li>{@link com.github.sneakytowelsuit.purerules.context.field.FieldContextKey} - 
 *       Unique key for cache entries combining input ID and field instance</li>
 * </ul>
 *
 * <p><strong>Caching Strategy:</strong>
 * Field values are cached using a composite key that includes:
 * <ul>
 *   <li><strong>Input ID:</strong> Unique identifier for the input instance being evaluated</li>
 *   <li><strong>Field Instance:</strong> The specific field extractor being used</li>
 * </ul>
 *
 * <p>This approach ensures that:
 * <ul>
 *   <li>Values are correctly isolated between different input instances</li>
 *   <li>Different field implementations are cached separately</li>
 *   <li>Cache hits occur when the same field is used multiple times on the same input</li>
 * </ul>
 *
 * <p><strong>Performance Example:</strong>
 * <pre>{@code
 * // Consider this rule group where AgeField is used twice:
 * RuleGroup<Person> ageRange = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .conditions(Arrays.asList(
 *         Rule.<Person, Integer>builder()
 *             .field(new AgeField())        // First extraction: person.getAge()
 *             .operator(new GreaterThanOperator<>())
 *             .value(18)
 *             .build(),
 *         Rule.<Person, Integer>builder()
 *             .field(new AgeField())        // Cached: no second extraction
 *             .operator(new LessThanOperator<>())
 *             .value(65)
 *             .build()
 *     ))
 *     .build();
 *
 * // Without caching: 2 calls to person.getAge()
 * // With caching: 1 call to person.getAge(), 1 cache hit
 * }</pre>
 *
 * <p><strong>Thread Safety:</strong>
 * The field context uses {@link java.util.concurrent.ConcurrentHashMap} to ensure thread-safe
 * access to cached values. Multiple threads can safely cache and retrieve field values 
 * simultaneously without external synchronization.
 *
 * <p><strong>Memory Management:</strong>
 * Field values are automatically cleared from the cache after evaluation completes to prevent
 * memory leaks. The cache can also be manually flushed for specific inputs or cleared entirely:
 *
 * <pre>{@code
 * // Automatic cleanup after evaluation
 * Map<String, Boolean> results = engine.evaluate(person);
 * // Field cache for 'person' is automatically cleared
 *
 * // Manual cache management
 * EngineContextService<Person, String> context = engine.getEngineContextService();
 * FieldContext<String> fieldContext = context.getFieldContext();
 * 
 * // Clear specific entries or entire cache as needed
 * context.flush(person);     // Clear cache for specific person
 * context.flushAll();        // Clear entire cache
 * }</pre>
 *
 * <p><strong>Cache Key Design:</strong>
 * The {@link com.github.sneakytowelsuit.purerules.context.field.FieldContextKey} is designed
 * to provide efficient equality and hashing for fast cache lookups while maintaining correct
 * isolation between different inputs and field types.
 *
 * <p><strong>Integration:</strong>
 * Field caching is seamlessly integrated into the evaluation process and requires no explicit
 * management by client code. The caching is transparent and automatic, activated whenever
 * rules are evaluated by the engine.
 *
 * @see com.github.sneakytowelsuit.purerules.context.EngineContextService
 * @see com.github.sneakytowelsuit.purerules.conditions.Field
 * @see com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService
 */
// spotless:on
package com.github.sneakytowelsuit.purerules.context.field;
