package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;

import java.time.LocalDate;

public class PlannedEndDate extends ValueType<LocalDate> {

    public PlannedEndDate(LocalDate value) {
        super(value);
    }

    public PlannedEndDate(String dateAsString) {
        super(LocalDate.parse(dateAsString));
    }
}
