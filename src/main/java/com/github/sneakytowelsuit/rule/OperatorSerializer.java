package com.github.sneakytowelsuit.rule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OperatorSerializer extends StdSerializer<Operator<?, ?>> {
    public OperatorSerializer() {
        this(null);
    }
    private OperatorSerializer(Class<Operator<?, ?>> t) {
        super(t);
    }
    @Override
    public void serialize(Operator<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeString(value.getClass().getSimpleName());
        gen.writeEndObject();
    }
}
