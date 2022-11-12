package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ValueType;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectTimespan {
    @NonNull
    StartDate startDate;
    @NonNull
    RelevantEndDate endDate;

    public static Optional<ProjectTimespan> of(Optional<StartDate> startDate, Optional<RelevantEndDate> relevantEndDate) {
        if (startDate.isEmpty() && relevantEndDate.isEmpty()) {
            return Optional.empty();
        }

        if (startDate.isPresent() && relevantEndDate.isPresent()) {
            return Optional.of(new ProjectTimespan(startDate.get(), relevantEndDate.get()));
        }

        return relevantEndDate
                .map(end ->
                        new ProjectTimespan(new StartDate(end.unbox()), end))
                .or(() -> startDate.map(start ->
                        new ProjectTimespan(start, new RelevantEndDate(start.unbox()))));
    }

    public boolean contains(LocalDate givenDate) {
        return !(givenDate.isBefore(startDate.unbox()) || givenDate.isAfter(endDate.unbox()));
    }

    public boolean contains(ProjectTimespan timespan) {
        return !timespan.getStartDate().unbox().isAfter(endDate.unbox()) && !startDate.unbox().isAfter(timespan.getEndDate().unbox());
    }
}
