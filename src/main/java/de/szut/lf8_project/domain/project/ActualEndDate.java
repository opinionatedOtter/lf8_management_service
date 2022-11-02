package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;

import java.time.LocalDate;
public class ActualEndDate extends ValueType<LocalDate> {

    public ActualEndDate(LocalDate actualEndDate) {
        super(actualEndDate);
    }
}
