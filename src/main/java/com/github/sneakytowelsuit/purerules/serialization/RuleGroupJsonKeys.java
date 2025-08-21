package com.github.sneakytowelsuit.purerules.serialization;

import lombok.Getter;

// spotless:off
/**
 * Defines the JSON property keys used for serializing and deserializing rule groups and rules.
 *
 * <p>This enum centralizes all JSON property names used in the serialization format, ensuring
 * consistency and making it easy to modify the serialization schema if needed.
 *
 * <p>The keys cover all aspects of rule group and rule serialization including:
 *
 * <ul>
 *   <li>Basic properties: ID, priority
 *   <li>Rule components: field, operator, value
 *   <li>Group structure: conditions, combinator, bias
 *   <li>Control flags: inverted
 *   <li>Type information: class names for deserialization
 * </ul>
 */
// spotless:on
@Getter
public enum RuleGroupJsonKeys {
  // spotless:off
  /** Unique identifier for rules and rule groups. */
  // spotless:on
  ID("id"),

  // spotless:off
  /** Priority/weight value for weighted evaluation. */
  // spotless:on
  PRIORITY("priority"),

  // spotless:off
  /** Field extractor specification for rules. */
  // spotless:on
  FIELD("field"),

  // spotless:off
  /** Operator specification for rules. */
  // spotless:on
  OPERATOR("operator"),

  // spotless:off
  /** Value specification for rules. */
  // spotless:on
  VALUE("value"),

  // spotless:off
  /** Class name for datatype of the field and value of a rule. */
  // spotless:on
  DATATYPE("datatype"),

  // spotless:off
  /** Array of conditions within a rule group. */
  // spotless:on
  CONDITIONS("conditions"),

  // spotless:off
  /** Inversion flag for rule groups. */
  // spotless:on
  INVERTED("inverted"),

  // spotless:off
  /** Bias setting for empty rule groups. */
  // spotless:on
  BIAS("bias"),

  // spotless:off
  /** Combinator (AND/OR) for rule groups. */
  // spotless:on
  COMBINATOR("combinator");

  // spotless:off
  /** The actual JSON key string. */
  // spotless:on
  private final String key;

  // spotless:off
  /**
   * Creates a new JSON key enum value.
   *
   * @param key the JSON property name to use in serialization
   */
  // spotless:on
  RuleGroupJsonKeys(String key) {
    this.key = key;
  }
}
