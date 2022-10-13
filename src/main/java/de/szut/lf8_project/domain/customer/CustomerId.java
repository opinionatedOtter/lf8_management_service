package de.szut.lf8_project.domain.customer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.szut.lf8_project.common.ValueType;
import de.szut.lf8_project.common.ValueTypeSerializer;

@JsonSerialize(using = ValueTypeSerializer.class)
public class CustomerId extends ValueType<Long> {
    public CustomerId(Long value) {
        super(value);
    }
}
