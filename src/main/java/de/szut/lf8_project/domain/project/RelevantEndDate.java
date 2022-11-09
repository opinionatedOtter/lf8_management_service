package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;
import lombok.NonNull;

import java.time.LocalDate;

public class RelevantEndDate extends ValueType<LocalDate> {
    public RelevantEndDate(@NonNull LocalDate generic) {
        super(generic);
    }
}
