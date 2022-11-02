package de.szut.lf8_project.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ValueTypeSerializer extends JsonSerializer<ValueType> {

    @Override
    public void serialize(ValueType valueType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        JsonSerializer<Object> serializer = serializerProvider.findValueSerializer(valueType.unbox().getClass());

        Object unboxedValue = valueType.unbox();

        serializer.serialize(unboxedValue, jsonGenerator, serializerProvider);
    }
}
