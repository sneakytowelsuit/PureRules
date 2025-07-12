package com.github.sneakytowelsuit.purerules.serialization;

import lombok.Getter;

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
@Getter
public enum RuleGroupJsonKeys {
  /** Unique identifier for rules and rule groups. */
  ID("id"),

  /** Priority/weight value for weighted evaluation. */
  PRIORITY("priority"),

  /** Field extractor specification for rules. */
  FIELD("field"),

  /** Operator specification for rules. */
  OPERATOR("operator"),

  /** Value specification for rules. */
  VALUE("value"),

  /** Class name for type information during deserialization. */
  VALUE_CLASS("class"),

  /** Array of conditions within a rule group. */
  CONDITIONS("conditions"),

  /** The actual value within a value specification. */
  VALUE_VALUE("value"),

  /** Inversion flag for rule groups. */
  INVERTED("inverted"),

  /** Bias setting for empty rule groups. */
  BIAS("bias"),

  /** Combinator (AND/OR) for rule groups. */
  COMBINATOR("combinator");

  /** The actual JSON key string. */
  private final String key;

  /**
   * Creates a new JSON key enum value.
   *
   * @param key the JSON property name to use in serialization
   */
  RuleGroupJsonKeys(String key) {
    this.key = key;
  }
}
