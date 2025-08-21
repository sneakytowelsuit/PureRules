package com.github.sneakytowelsuit.purerules.engine;

import com.github.sneakytowelsuit.purerules.conditions.Condition;
import com.github.sneakytowelsuit.purerules.context.EngineContextService;
import com.github.sneakytowelsuit.purerules.evaluation.DeterministicEvaluationService;
import com.github.sneakytowelsuit.purerules.evaluation.IEvaluationService;
import com.github.sneakytowelsuit.purerules.evaluation.ProbabilisticEvaluationService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// spotless:off
/**
 * The main rules engine for evaluating conditions against input data.
 *
 * <p>PureRulesEngine is a flexible, JVM-based rules engine that provides both deterministic boolean
 * evaluation and probabilistic scoring capabilities. The engine is immutable after construction,
 * ensuring thread safety and predictable behavior across multiple evaluations.
 *
 * <p>Key features:
 * <ul>
 *   <li><strong>Deterministic Mode:</strong> Strict boolean evaluation using exact rule matching
 *   <li><strong>Probabilistic Mode:</strong> Weighted scoring with probability thresholds
 *   <li><strong>Field Value Caching:</strong> Optimized performance through value caching
 *   <li><strong>Context Management:</strong> Comprehensive evaluation context for debugging
 *   <li><strong>Thread Safety:</strong> Immutable design allows concurrent usage
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 *
 * <p>Creating a deterministic engine:
 * <pre>{@code
 * // Define your input type and conditions
 * List<Condition<Person>> conditions = List.of(
 *     Rule.<Person, Integer>builder()
 *         .field(new AgeField())
 *         .operator(new GreaterThanOperator<>())
 *         .value(18)
 *         .build()
 * );
 *
 * // Create deterministic engine
 * PureRulesEngine<Person, String> engine = PureRulesEngine
 *     .getDeterministicEngine(Person::getId, conditions);
 *
 * // Evaluate
 * Person person = new Person("john", 25);
 * Map<String, Boolean> results = engine.evaluate(person);
 * }</pre>
 *
 * <p>Creating a probabilistic engine:
 * <pre>{@code
 * // Create probabilistic engine with 70% threshold
 * PureRulesEngine<Person, String> engine = PureRulesEngine
 *     .getProbabilisticEngine(Person::getId, 0.7f, conditions);
 *
 * // Results will be true only if probability score >= 0.7
 * Map<String, Boolean> results = engine.evaluate(person);
 * }</pre>
 *
 * <p>Batch evaluation:
 * <pre>{@code
 * List<Person> people = Arrays.asList(person1, person2, person3);
 * Map<String, Map<String, Boolean>> batchResults = engine.evaluateAll(people);
 * }</pre>
 *
 * @param <TInput> the type of input data to evaluate against rules
 * @param <TInputId> the type used to uniquely identify input instances for context management
 * 
 * @see com.github.sneakytowelsuit.purerules.conditions.Rule
 * @see com.github.sneakytowelsuit.purerules.conditions.RuleGroup
 * @see com.github.sneakytowelsuit.purerules.conditions.Condition
 */
// spotless:on
public class PureRulesEngine<TInput, TInputId> {
  // spotless:off
  /** The list of conditions (rules and rule groups) configured for this engine. */
  // spotless:on
  private final List<Condition<TInput>> conditions;

  // spotless:off
  /**
   * The evaluation service used to evaluate the rules based on the engine mode. This service
   * encapsulates the logic for evaluating conditions and combining results.
   */
  // spotless:on
  private final IEvaluationService<TInput, TInputId> evaluationService;

  // spotless:off
  /** 
   * The context service for managing field value caching and evaluation state tracking.
   */
  // spotless:on
  private final EngineContextService<TInput, TInputId> engineContextService;

  // spotless:off
  /**
   * Creates a new probabilistic rules engine with weighted scoring evaluation.
   *
   * <p>In probabilistic mode, rules are evaluated using their weights to calculate a confidence
   * score. The result is true only if the calculated probability meets or exceeds the specified
   * minimum threshold.
   *
   * <p><strong>Example:</strong>
   * <pre>{@code
   * // Create rules with different weights
   * List<Condition<Person>> conditions = Arrays.asList(
   *     Rule.<Person, Integer>builder()
   *         .field(new AgeField())
   *         .operator(new GreaterThanOperator<>())
   *         .value(18)
   *         .weight(2)  // Higher weight
   *         .build(),
   *     Rule.<Person, String>builder()
   *         .field(new CountryField())
   *         .operator(new EqualsOperator<>())
   *         .value("US")
   *         .weight(1)  // Lower weight
   *         .build()
   * );
   *
   * // Engine with 60% confidence threshold
   * var engine = PureRulesEngine.getProbabilisticEngine(
   *     Person::getId, 0.6f, conditions);
   * }</pre>
   *
   * @param <T> the type of input data to evaluate
   * @param <I> the type used to identify input instances
   * @param inputIdGetter function to extract unique identifiers from input instances
   * @param minimumProbabilityThreshold minimum confidence required for a positive result (0.0 to 1.0)
   * @param conditions the list of conditions to evaluate
   * @return a new probabilistic rules engine
   */
  // spotless:on
  public static <T, I> PureRulesEngine<T, I> getProbabilisticEngine(
      Function<T, I> inputIdGetter,
      Float minimumProbabilityThreshold,
      List<Condition<T>> conditions) {
    return new PureRulesEngine<>(inputIdGetter, minimumProbabilityThreshold, conditions);
  }

  // spotless:off
  /**
   * Creates a new instance of PureRulesEngine with the specified rule groups and minimum
   * probability threshold for PROBABILISTIC mode.
   *
   * @param inputIdGetter function to extract unique identifiers from input instances for context management
   * @param minimumProbabilityThreshold the minimum probability threshold for the PROBABILISTIC
   *     engine mode. Results below this threshold will be considered false
   * @param conditions the list of conditions to be evaluated by the engine
   */
  // spotless:on
  private PureRulesEngine(
      Function<TInput, TInputId> inputIdGetter,
      Float minimumProbabilityThreshold,
      List<Condition<TInput>> conditions) {
    this.conditions = conditions;
    // spotless:off
    /**
     * The minimum probability threshold for the PROBABILISTIC engine mode. If the calculated
     * probability is below this threshold, the result will be considered false.
     */
    // spotless:on
    this.evaluationService =
        new ProbabilisticEvaluationService<>(conditions, minimumProbabilityThreshold);
    this.engineContextService = new EngineContextService<>(inputIdGetter);
  }

  // spotless:off
  /**
   * Creates a new deterministic rules engine with boolean evaluation.
   *
   * <p>In deterministic mode, each condition is evaluated as a strict boolean operation. Rules
   * must match exactly, and rule groups combine results using their configured combinators
   * (AND/OR logic) without any weighted scoring.
   *
   * <p><strong>Example:</strong>
   * <pre>{@code
   * // Create conditions for exact matching
   * List<Condition<Person>> conditions = Arrays.asList(
   *     Rule.<Person, Integer>builder()
   *         .field(new AgeField())
   *         .operator(new GreaterThanOperator<>())
   *         .value(18)
   *         .build(),
   *     RuleGroup.<Person>builder()
   *         .combinator(Combinator.AND)
   *         .conditions(Arrays.asList(
   *             // Nested rules...
   *         ))
   *         .build()
   * );
   *
   * var engine = PureRulesEngine.getDeterministicEngine(
   *     Person::getId, conditions);
   * }</pre>
   *
   * @param <T> the type of input data to evaluate
   * @param <I> the type used to identify input instances
   * @param inputIdGetter function to extract unique identifiers from input instances
   * @param conditions the list of conditions to evaluate
   * @return a new deterministic rules engine
   */
  // spotless:on
  public static <T, I> PureRulesEngine<T, I> getDeterministicEngine(
      Function<T, I> inputIdGetter, List<Condition<T>> conditions) {
    return new PureRulesEngine<>(inputIdGetter, conditions);
  }

  // spotless:off
  /**
   * Creates a new instance of PureRulesEngine for DETERMINISTIC mode.
   *
   * @param inputIdGetter function to extract unique identifiers from input instances for context management
   * @param conditions the list of conditions to be evaluated by the engine
   */
  // spotless:on
  private PureRulesEngine(
      Function<TInput, TInputId> inputIdGetter, List<Condition<TInput>> conditions) {
    this.conditions = conditions;
    this.evaluationService = new DeterministicEvaluationService<>(conditions);
    this.engineContextService = new EngineContextService<>(inputIdGetter);
  }

  // spotless:off
  /**
   * Gets the evaluation service configured for this engine.
   *
   * @return the evaluation service handling rule processing logic
   */
  // spotless:on
  private IEvaluationService<TInput, TInputId> getEvaluationService() {
    return this.evaluationService;
  }

  // spotless:off
  /**
   * Gets the context service for field value caching and evaluation tracking.
   *
   * @return the engine context service managing evaluation state
   */
  // spotless:on
  private EngineContextService<TInput, TInputId> getEngineContextService() {
    return this.engineContextService;
  }

  // spotless:off
  /**
   * Evaluates all configured conditions against the provided input data.
   *
   * <p>This is the primary method for rule evaluation. It processes each condition defined in the
   * engine and returns a map indicating which conditions were satisfied by the input.
   *
   * <p>The evaluation process:
   * <ol>
   *   <li>Extracts field values from the input using defined field extractors
   *   <li>Applies operators to compare field values against rule criteria
   *   <li>Combines individual rule results using rule group logic
   *   <li>Returns a map of condition IDs to boolean results
   *   <li>Clears evaluation context to prevent memory leaks
   * </ol>
   *
   * <p><strong>Example usage:</strong>
   * <pre>{@code
   * Person person = new Person("john", 25, "US");
   * Map<String, Boolean> results = engine.evaluate(person);
   *
   * // Check specific condition results
   * Boolean ageCheckPassed = results.get("age-rule-id");
   * Boolean locationCheckPassed = results.get("location-rule-group-id");
   * }</pre>
   *
   * @param input the input data to evaluate against the configured conditions
   * @return a map where keys are condition IDs and values indicate whether each condition
   *         was satisfied (true) or not satisfied (false). Returns an empty map if evaluation
   *         fails or no conditions are configured.
   */
  // spotless:on
  public Map<String, Boolean> evaluate(TInput input) {
    Map<String, Boolean> results =
        this.getEvaluationService().evaluate(input, this.getEngineContextService());
    if (results == null) {
      return Collections.emptyMap();
    }
    // Clear the context after evaluation to avoid memory leaks
    this.getEngineContextService().flush(input);
    return results;
  }

  // spotless:off
  /**
   * Evaluates all configured conditions against a list of input data items.
   *
   * <p>This method provides convenient batch processing capabilities, evaluating each input
   * item individually and collecting the results into a single map structure.
   *
   * <p><strong>Example usage:</strong>
   * <pre>{@code
   * List<Person> people = Arrays.asList(
   *     new Person("john", 25, "US"),
   *     new Person("jane", 17, "CA"),
   *     new Person("bob", 30, "UK")
   * );
   *
   * Map<String, Map<String, Boolean>> batchResults = engine.evaluateAll(people);
   *
   * // Access results for specific person
   * Map<String, Boolean> johnResults = batchResults.get("john");
   * Boolean johnAgeCheck = johnResults.get("age-rule-id");
   * }</pre>
   *
   * @param inputs the list of input data items to evaluate
   * @return a map where keys are input IDs (extracted using the configured inputIdGetter function)
   *         and values are the evaluation results for each input (same format as {@link #evaluate(Object)})
   */
  // spotless:on
  public Map<TInputId, Map<String, Boolean>> evaluateAll(List<TInput> inputs) {
    return inputs.stream()
        .collect(
            Collectors.toMap(
                input -> this.engineContextService.getInputIdGetter().apply(input),
                this::evaluate));
  }
}
