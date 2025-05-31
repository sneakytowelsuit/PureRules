package com.github.sneakytowelsuit.purerules.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.conditions.RuleGroup;
import com.github.sneakytowelsuit.purerules.exceptions.RuleGroupSerializationException;
import java.io.IOException;

public class RuleGroupSerializer extends StdSerializer<RuleGroup<?>> {
  public RuleGroupSerializer() {
    this((Class<RuleGroup<?>>) RuleGroup.builder().build().getClass());
  }

  private RuleGroupSerializer(Class<RuleGroup<?>> t) {
    super(t);
  }

  @Override
  public void serialize(RuleGroup<?> value, JsonGenerator gen, SerializerProvider provider) {
    try {
      if (value == null) {
        throw new NullPointerException("RuleGroup cannot be null");
      }
      if (value.getCombinator() == null) {
        throw new NullPointerException("Combinator cannot be null");
      }
      if (value.getBias() == null) {
        throw new NullPointerException("Bias cannot be null");
      }
      if (value.getConditions() == null) {
        throw new NullPointerException("Conditions cannot be null");
      }
      gen.writeStartObject();
      gen.writeBooleanField(RuleGroupJsonKeys.INVERTED.getKey(), value.isInverted());
      gen.writeStringField(RuleGroupJsonKeys.COMBINATOR.getKey(), value.getCombinator().name());
      gen.writeStringField(RuleGroupJsonKeys.BIAS.getKey(), value.getBias().name());
      gen.writeArrayFieldStart(RuleGroupJsonKeys.CONDITIONS.getKey());
      value
          .getConditions()
          .forEach(
              condition -> {
                if (condition == null) {
                  throw new RuleGroupSerializationException(
                      "Condition in RuleGroup cannot be null");
                }
                switch (condition) {
                  case Rule rule -> {
                    try {
                      provider.findValueSerializer(Rule.class).serialize(rule, gen, provider);
                    } catch (IOException e) {
                      throw new RuleGroupSerializationException(
                          "Exception serializing Rule in RuleGroup", e);
                    }
                  }
                  case RuleGroup ruleGroup -> {
                    try {
                      provider
                          .findValueSerializer(RuleGroup.class)
                          .serialize(ruleGroup, gen, provider);
                    } catch (IOException e) {
                      throw new RuleGroupSerializationException(
                          "Exception serializing nested RuleGroup", e);
                    }
                  }
                  default ->
                      throw new RuleGroupSerializationException(
                          "Unknown condition type in RuleGroup: " + condition.getClass());
                }
              });
      gen.writeEndArray();
      gen.writeEndObject();
    } catch (Exception e) {
      throw new RuleGroupSerializationException(
          "Exception encountered while serializing RuleGroup", e);
    }
  }
}
