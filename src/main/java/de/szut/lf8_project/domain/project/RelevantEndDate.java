package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Optional;

public class RelevantEndDate extends ValueType<LocalDate> {
    public RelevantEndDate(@NonNull LocalDate generic) {
        super(generic);
    }

    public static Optional<RelevantEndDate> of(Optional<PlannedEndDate> plannedEndDate, Optional<ActualEndDate> actualEndDate){
        if(actualEndDate.isEmpty() && plannedEndDate.isEmpty()){
            return Optional.empty();
        }
        return actualEndDate.map((date) -> new RelevantEndDate(date.unbox())).or(() -> Optional.of(new RelevantEndDate(plannedEndDate.get().unbox())));
    }
}
