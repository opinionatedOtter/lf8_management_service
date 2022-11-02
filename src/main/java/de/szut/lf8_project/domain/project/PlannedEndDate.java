package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;

import java.time.LocalDate;
import java.util.Date;

public class PlannedEndDate extends ValueType<LocalDate> {

    public PlannedEndDate(LocalDate value) {
        super(value);
    }
}
