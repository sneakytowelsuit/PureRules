package com.github.sneakytowelsuit.purerules.conditions;

import com.github.sneakytowelsuit.purerules.engine.EngineMode;

// spotless:off
/**
 * Represents a logical condition that can be evaluated. This interface is sealed and only permits
 * {@link Rule} and {@link RuleGroup} as implementations.
 *
 * <p>Conditions are the basic building blocks of the PureRules evaluation system. They provide
 * a unified interface for both individual rules and groups of conditions, enabling flexible
 * composition and evaluation strategies.
 *
 * <p><strong>Design Pattern:</strong>
 * The Condition interface follows the Composite pattern, where:
 * <ul>
 *   <li>{@link Rule} represents a leaf condition (individual comparison)</li>
 *   <li>{@link RuleGroup} represents a composite condition (collection of conditions)</li>
 * </ul>
 *
 * <p><strong>Usage in Engine:</strong>
 * <pre>{@code
 * // Mix rules and rule groups in condition list
 * List<Condition<Person>> conditions = Arrays.asList(
 *     Rule.<Person, Integer>builder()
 *         .field(new AgeField())
 *         .operator(new GreaterThanOperator<>())
 *         .value(18)
 *         .build(),
 *         
 *     RuleGroup.<Person>builder()
 *         .combinator(Combinator.OR)
 *         .conditions(Arrays.asList(
 *             veteranRule,
 *             seniorRule
 *         ))
 *         .build()
 * );
 *
 * PureRulesEngine<Person, String> engine = 
 *     PureRulesEngine.getDeterministicEngine(Person::getId, conditions);
 * }</pre>
 *
 * <p><strong>Identification:</strong>
 * Each condition has a unique identifier that appears in evaluation results, allowing you to
 * track which specific conditions were satisfied:
 * <pre>{@code
 * Map<String, Boolean> results = engine.evaluate(person);
 * 
 * // Check specific condition results by ID
 * Boolean ageCheckResult = results.get(ageRule.getId());
 * Boolean eligibilityGroupResult = results.get(eligibilityGroup.getId());
 * }</pre>
 *
 * <p><strong>Weight Usage:</strong>
 * Condition weights affect evaluation differently based on engine mode:
 * <ul>
 *   <li><strong>Deterministic Mode:</strong> Weights are ignored, only boolean logic applies</li>
 *   <li><strong>Probabilistic Mode:</strong> Higher weights increase influence on confidence score</li>
 * </ul>
 *
 * <pre>{@code
 * // In probabilistic mode, this rule has 3x the influence of weight=1 rules
 * Rule<Person, String> criticalRule = Rule.<Person, String>builder()
 *     .field(new SecurityLevelField())
 *     .operator(new EqualsOperator<>())
 *     .value("TOP_SECRET")
 *     .weight(3)
 *     .build();
 * }</pre>
 *
 * @param <InputType> the type of input data that this condition can evaluate
 * 
 * @see Rule
 * @see RuleGroup
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 * @see com.github.sneakytowelsuit.purerules.engine.EngineMode
 */
// spotless:on
public sealed interface Condition<InputType> permits Rule, RuleGroup {
  // spotless:off
  /**
   * Gets the unique identifier for this condition.
   * 
   * <p>The ID is used to correlate evaluation results with specific conditions. In evaluation
   * result maps, this ID serves as the key to access the boolean result for this condition.
   *
   * <p>IDs are automatically generated using UUIDs by default, but can be customized during
   * condition construction for more meaningful tracking.
   *
   * @return the unique identifier string for this condition
   */
  // spotless:on
  public String getId();

  // spotless:off
  /**
   * Gets the priority of this condition. Higher priority conditions are weighted heavier when
   * evaluating in probabilistic mode.
   *
   * <p>Weight behavior by evaluation mode:
   * <ul>
   *   <li>{@link EngineMode#DETERMINISTIC} ignores priority altogether - only boolean logic applies</li>
   *   <li>{@link EngineMode#PROBABILISTIC} respects priority to calculate balance while computing
   *       the confidence score</li>
   * </ul>
   *
   * <p><strong>Probabilistic Weight Example:</strong>
   * <pre>{@code
   * // These conditions contribute differently to overall confidence:
   * Rule<Person, String> highPriorityRule = Rule.<Person, String>builder()
   *     .weight(5)    // 5x influence
   *     .field(new CriticalField())
   *     // ... other configuration
   *     .build();
   *     
   * Rule<Person, String> normalRule = Rule.<Person, String>builder()
   *     .weight(1)    // 1x influence (default)
   *     .field(new OptionalField())
   *     // ... other configuration  
   *     .build();
   *
   * // In probabilistic mode with 70% threshold:
   * // - If highPriorityRule passes and normalRule fails: likely overall pass
   * // - If highPriorityRule fails and normalRule passes: likely overall fail
   * }</pre>
   *
   * @return the priority weight of this condition, or 1 to represent baseline priority
   */
  // spotless:on
  public Integer getWeight();
}
