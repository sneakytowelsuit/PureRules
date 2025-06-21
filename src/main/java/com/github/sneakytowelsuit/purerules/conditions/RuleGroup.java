package com.github.sneakytowelsuit.purerules.conditions;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a group of conditions (rules or nested rule groups) that are evaluated together using
 * a specified combinator (AND/OR), with optional inversion and bias. Each group is uniquely
 * identified. Default values are set to ensure safe evaluation, but can be overridden via the
 * builder.
 *
 * @param <TInput> the type of input to evaluate
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
  @Builder.Default private final Integer priority = 1;

  /** Bias to use when the group contains no conditions. Defaults to EXCLUSIVE (pessimistic). */
  @Builder.Default private final Bias bias = Bias.EXCLUSIVE;
}
