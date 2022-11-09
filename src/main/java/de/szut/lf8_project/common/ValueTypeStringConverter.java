package de.szut.lf8_project.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public abstract class ValueTypeStringConverter<T> implements AttributeConverter<ValueType<String>, String> {

    @Override
    public String convertToDatabaseColumn(final ValueType<String> attribute) {
        return attribute.unbox();
    }
}

