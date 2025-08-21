package com.github.sneakytowelsuit.purerules.conditions;

import java.util.UUID;
import lombok.*;

/**
 * Represents a single rule condition that evaluates an input using a field extractor, an operator,
 * and a comparison value. The rule is uniquely identified and can be evaluated in the context of a
 * rule group or independently.
 *
 * <p>Rules are the fundamental building blocks of the PureRules engine. Each rule consists of:
 * <ul>
 *   <li><strong>Field:</strong> Extracts a value from the input data</li>
 *   <li><strong>Operator:</strong> Defines how to compare the extracted value</li>
 *   <li><strong>Value:</strong> The target value to compare against</li>
 *   <li><strong>Weight:</strong> Priority for probabilistic evaluation (default: 1)</li>
 *   <li><strong>ID:</strong> Unique identifier for tracking evaluation results</li>
 * </ul>
 *
 * <p><strong>Basic Rule Examples:</strong>
 *
 * <p>Simple equality rule:
 * <pre>{@code
 * Rule<Person, String> nameRule = Rule.<Person, String>builder()
 *     .field(new NameField())
 *     .operator(new EqualsOperator<>())
 *     .value("John")
 *     .build();
 * }</pre>
 *
 * <p>Numeric comparison rule:
 * <pre>{@code
 * Rule<Person, Integer> ageRule = Rule.<Person, Integer>builder()
 *     .field(new AgeField())
 *     .operator(new GreaterThanOperator<>())
 *     .value(18)
 *     .weight(2)  // Higher weight for probabilistic evaluation
 *     .build();
 * }</pre>
 *
 * <p>String matching rule:
 * <pre>{@code
 * Rule<Person, String> locationRule = Rule.<Person, String>builder()
 *     .field(new CityField())
 *     .operator(new StringContainsCaseInsensitiveOperator())
 *     .value("New")  // Matches "New York", "New Orleans", etc.
 *     .id("city-contains-new")  // Custom ID for tracking
 *     .build();
 * }</pre>
 *
 * <p><strong>Complex Field Rules:</strong>
 * <pre>{@code
 * // Rule with computed field
 * Rule<Person, Boolean> adultRule = Rule.<Person, Boolean>builder()
 *     .field(new AdultStatusField())  // Computes person.getAge() >= 18
 *     .operator(new EqualsOperator<>())
 *     .value(true)
 *     .build();
 *
 * // Rule with nested object access
 * Rule<Person, String> addressRule = Rule.<Person, String>builder()
 *     .field(new AddressCityField())  // person.getAddress().getCity()
 *     .operator(new EqualsOperator<>())
 *     .value("Seattle")
 *     .build();
 * }</pre>
 *
 * <p><strong>Usage in Rule Groups:</strong>
 * <pre>{@code
 * RuleGroup<Person> adultInSeattle = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .conditions(Arrays.asList(
 *         ageRule,        // Age > 18
 *         addressRule     // City = Seattle
 *     ))
 *     .build();
 * }</pre>
 *
 * <p><strong>Weight Usage:</strong>
 * In deterministic mode, weights are ignored. In probabilistic mode, higher weights contribute
 * more to the overall confidence score:
 * <pre>{@code
 * // High-priority rule
 * Rule<Person, String> criticalRule = Rule.<Person, String>builder()
 *     .field(new SecurityClearanceField())
 *     .operator(new EqualsOperator<>())
 *     .value("TOP_SECRET")
 *     .weight(10)  // Much higher weight
 *     .build();
 *
 * // Low-priority rule  
 * Rule<Person, String> preferenceRule = Rule.<Person, String>builder()
 *     .field(new PreferredColorField())
 *     .operator(new EqualsOperator<>())
 *     .value("blue")
 *     .weight(1)   // Standard weight
 *     .build();
 * }</pre>
 *
 * @param <TInput> the type of input data to evaluate against this rule
 * @param <TValue> the type of value extracted by the field and used for comparison
 * 
 * @see Field
 * @see Operator
 * @see RuleGroup
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 */
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public final class Rule<TInput, TValue> implements Condition<TInput> {
  private static final String RULE_ID_PREFIX = "rule-";
  @Builder.Default private final String id = RULE_ID_PREFIX + UUID.randomUUID().toString();
  private final Field<TInput, TValue> field;
  private final Operator<TValue> operator;
  private final TValue value;
  @Builder.Default private final Integer weight = 1;
}
