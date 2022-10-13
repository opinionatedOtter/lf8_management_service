package de.szut.lf8_project.domain.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.szut.lf8_project.common.ValueType;
import de.szut.lf8_project.common.ValueTypeSerializer;

@JsonSerialize(using = ValueTypeSerializer.class)
public class ProjectDescription extends ValueType<String> {
    public ProjectDescription(String value) {
        super(value);
    }
}
