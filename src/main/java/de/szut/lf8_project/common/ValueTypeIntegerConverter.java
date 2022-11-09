package de.szut.lf8_project.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public abstract class ValueTypeIntegerConverter<T> implements AttributeConverter<ValueType<Integer>, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final ValueType<Integer> attribute) {
        return attribute.unbox();
    }
}

