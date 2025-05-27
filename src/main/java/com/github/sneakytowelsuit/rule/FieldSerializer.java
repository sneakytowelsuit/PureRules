package com.github.sneakytowelsuit.rule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FieldSerializer extends StdSerializer<Field<?, ?>> {
    public FieldSerializer() {
        this(null);
    }
    private FieldSerializer(Class<Field<?, ?>> t) {
        super(t);
    }
    @Override
    public void serialize(Field<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writePOJO(value.getClass().getSimpleName());
        gen.writeEndObject();
    }
}
