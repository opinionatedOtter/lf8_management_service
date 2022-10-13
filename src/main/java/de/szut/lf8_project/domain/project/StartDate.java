package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;

import java.time.LocalDate;

public class StartDate extends ValueType<LocalDate> {

    public StartDate(LocalDate value){
        super(value);
    }

    public StartDate(String value){
        super(LocalDate.parse(value));
    }
}
