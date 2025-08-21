package com.github.sneakytowelsuit.purerules.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

/**
 * Represents a group of conditions (rules or nested rule groups) that are evaluated together using
 * a specified combinator (AND/OR), with optional inversion and bias. Each group is uniquely
 * identified. Default values are set to ensure safe evaluation, but can be overridden via the
 * builder.
 *
 * <p>RuleGroups enable complex conditional logic by combining multiple conditions with logical
 * operators. They support:
 * <ul>
 *   <li><strong>Combinators:</strong> AND/OR logic for combining child conditions</li>
 *   <li><strong>Nesting:</strong> Rule groups can contain other rule groups for hierarchical logic</li>
 *   <li><strong>Inversion:</strong> NOT logic by inverting the group's result</li>
 *   <li><strong>Bias:</strong> Default behavior for empty groups</li>
 *   <li><strong>Weighting:</strong> Priority for probabilistic evaluation</li>
 * </ul>
 *
 * <p><strong>Basic Rule Group Examples:</strong>
 *
 * <p>Simple AND group:
 * <pre>{@code
 * RuleGroup<Person> adultUSCitizen = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .conditions(Arrays.asList(
 *         Rule.<Person, Integer>builder()
 *             .field(new AgeField())
 *             .operator(new GreaterThanOperator<>())
 *             .value(18)
 *             .build(),
 *         Rule.<Person, String>builder()
 *             .field(new CountryField())
 *             .operator(new EqualsOperator<>())
 *             .value("US")
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 *
 * <p>Simple OR group:
 * <pre>{@code
 * RuleGroup<Person> seniorOrVeteran = RuleGroup.<Person>builder()
 *     .combinator(Combinator.OR)
 *     .conditions(Arrays.asList(
 *         Rule.<Person, Integer>builder()
 *             .field(new AgeField())
 *             .operator(new GreaterThanOperator<>())
 *             .value(65)
 *             .build(),
 *         Rule.<Person, Boolean>builder()
 *             .field(new VeteranStatusField())
 *             .operator(new EqualsOperator<>())
 *             .value(true)
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 *
 * <p><strong>Inverted Groups:</strong>
 * <pre>{@code
 * // NOT (age > 65 OR veteran = true) = young non-veteran
 * RuleGroup<Person> youngNonVeteran = RuleGroup.<Person>builder()
 *     .combinator(Combinator.OR)
 *     .isInverted(true)  // Inverts the result
 *     .conditions(Arrays.asList(
 *         ageOver65Rule,
 *         veteranRule
 *     ))
 *     .build();
 * }</pre>
 *
 * <p><strong>Nested Rule Groups:</strong>
 * <pre>{@code
 * // Complex logic: (age >= 18 AND country = "US") OR (veteran = true)
 * RuleGroup<Person> complexEligibility = RuleGroup.<Person>builder()
 *     .combinator(Combinator.OR)
 *     .conditions(Arrays.asList(
 *         // Nested AND group
 *         RuleGroup.<Person>builder()
 *             .combinator(Combinator.AND)
 *             .conditions(Arrays.asList(
 *                 ageRule,
 *                 countryRule
 *             ))
 *             .build(),
 *         // Single rule
 *         veteranRule
 *     ))
 *     .build();
 * }</pre>
 *
 * <p><strong>Empty Group Behavior with Bias:</strong>
 * <pre>{@code
 * // Empty group with inclusive bias (returns true when no conditions)
 * RuleGroup<Person> defaultTrue = RuleGroup.<Person>builder()
 *     .bias(Bias.INCLUSIVE)    // Returns true for empty group
 *     .conditions(Collections.emptyList())
 *     .build();
 *
 * // Empty group with exclusive bias (returns false when no conditions)  
 * RuleGroup<Person> defaultFalse = RuleGroup.<Person>builder()
 *     .bias(Bias.EXCLUSIVE)    // Returns false for empty group
 *     .conditions(Collections.emptyList())
 *     .build();
 * }</pre>
 *
 * <p><strong>Weighted Groups for Probabilistic Evaluation:</strong>
 * <pre>{@code
 * // High-priority group
 * RuleGroup<Person> criticalChecks = RuleGroup.<Person>builder()
 *     .combinator(Combinator.AND)
 *     .weight(10)  // Higher weight in probabilistic mode
 *     .conditions(Arrays.asList(
 *         securityClearanceRule,
 *         backgroundCheckRule
 *     ))
 *     .build();
 *
 * // Optional preference group
 * RuleGroup<Person> preferences = RuleGroup.<Person>builder()
 *     .combinator(Combinator.OR)
 *     .weight(1)   // Standard weight
 *     .conditions(Arrays.asList(
 *         preferredLocationRule,
 *         preferredRoleRule
 *     ))
 *     .build();
 * }</pre>
 *
 * <p><strong>Real-world Complex Example:</strong>
 * <pre>{@code
 * // Loan approval logic:
 * // (creditScore >= 700 AND income >= 50000) OR 
 * // (creditScore >= 650 AND income >= 75000 AND hasCollateral = true) OR
 * // (hasCoSigner = true AND coSignerCreditScore >= 750)
 *
 * RuleGroup<LoanApplication> loanApproval = RuleGroup.<LoanApplication>builder()
 *     .combinator(Combinator.OR)
 *     .conditions(Arrays.asList(
 *         // Standard qualification
 *         RuleGroup.<LoanApplication>builder()
 *             .combinator(Combinator.AND)
 *             .conditions(Arrays.asList(
 *                 creditScoreRule(700),
 *                 incomeRule(50000)
 *             ))
 *             .build(),
 *         
 *         // Lower credit with higher income and collateral
 *         RuleGroup.<LoanApplication>builder()
 *             .combinator(Combinator.AND)
 *             .conditions(Arrays.asList(
 *                 creditScoreRule(650),
 *                 incomeRule(75000),
 *                 collateralRule(true)
 *             ))
 *             .build(),
 *             
 *         // Co-signer path
 *         RuleGroup.<LoanApplication>builder()
 *             .combinator(Combinator.AND)
 *             .conditions(Arrays.asList(
 *                 hasCoSignerRule(true),
 *                 coSignerCreditRule(750)
 *             ))
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 *
 * @param <TInput> the type of input data to evaluate against this rule group
 * 
 * @see Rule
 * @see Condition
 * @see Combinator
 * @see Bias
 * @see com.github.sneakytowelsuit.purerules.engine.PureRulesEngine
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public final class RuleGroup<TInput> implements Condition<TInput> {
  private static final String RULE_GROUP_ID_PREFIX = "rule-group-";
  @Builder.Default private final String id = RULE_GROUP_ID_PREFIX + UUID.randomUUID().toString();
  @Builder.Default private final List<Condition<TInput>> conditions = new ArrayList<>();
  @Builder.Default private final Combinator combinator = Combinator.AND;
  @Builder.Default private final boolean isInverted = false;
  @Builder.Default private final Integer weight = 1;

  /** Bias to use when the group contains no conditions. Defaults to EXCLUSIVE (pessimistic). */
  @Builder.Default private final Bias bias = Bias.EXCLUSIVE;
}
