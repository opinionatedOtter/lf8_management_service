package de.szut.lf8_project.domain.project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.szut.lf8_project.common.ValueType;
import de.szut.lf8_project.common.ValueTypeSerializer;

@JsonSerialize(using = ValueTypeSerializer.class)
public class ProjectName extends ValueType<String> {
    public ProjectName(String value) {
        super(value);
    }
}
