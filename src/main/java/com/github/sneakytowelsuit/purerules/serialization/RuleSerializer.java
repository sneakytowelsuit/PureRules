package com.github.sneakytowelsuit.purerules.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.sneakytowelsuit.purerules.conditions.Rule;
import com.github.sneakytowelsuit.purerules.exceptions.RuleSerializationException;

public class RuleSerializer extends StdSerializer<Rule<?, ?>> {
  public RuleSerializer() {
    this((Class<Rule<?, ?>>) Rule.builder().build().getClass());
  }

  private RuleSerializer(Class<Rule<?, ?>> t) {
    super(t);
  }

  @Override
  public void serialize(Rule<?, ?> value, JsonGenerator gen, SerializerProvider provider) {
    try {
      gen.writeStartObject();

      if (value.getField() == null) {
        throw new NullPointerException("Field cannot be null");
      }
      gen.writeStringField(RuleGroupJsonKeys.FIELD.getKey(), value.getField().getClass().getName());

      if (value.getOperator() == null) {
        throw new NullPointerException("Operator cannot be null");
      }
      gen.writeStringField(
          RuleGroupJsonKeys.OPERATOR.getKey(), value.getOperator().getClass().getName());

      if (value.getValue() == null) {
        throw new NullPointerException("Value cannot be null");
      }
      gen.writeFieldName(RuleGroupJsonKeys.VALUE.getKey());
      gen.writeStartObject();
      gen.writeStringField(
          RuleGroupJsonKeys.VALUE_CLASS.getKey(), value.getValue().getClass().getName());
      gen.writePOJOField(RuleGroupJsonKeys.VALUE_VALUE.getKey(), value.getValue());
      gen.writeEndObject();
      gen.writeEndObject();
    } catch (Exception e) {
      throw new RuleSerializationException("Exception encountered while serializing RuleGroup", e);
    }
  }
}
