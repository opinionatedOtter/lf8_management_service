package de.szut.lf8_project.domain;

import de.szut.lf8_project.domain.project.ProjectTimespan;
import de.szut.lf8_project.domain.project.RelevantEndDate;
import de.szut.lf8_project.domain.project.StartDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("A Project Timespan should")
public class TestProjectTimespan {

    Optional<StartDate> startDate = Optional.of(new StartDate("2020-02-02"));
    Optional<RelevantEndDate> relevantEndDate = Optional.of(new RelevantEndDate(LocalDate.of(2021, 1, 1)));
    ProjectTimespan defaultTimespan = ProjectTimespan.of(startDate, relevantEndDate).get();

    @Test
    @DisplayName("return true if it conflicts with another timespan")
    public void assertTrueTimespan() {
        ProjectTimespan conflictingTimespan = ProjectTimespan.of(
                Optional.of(new StartDate("2020-03-03")),
                Optional.of(new RelevantEndDate(LocalDate.of(2020, 10, 10)))
        ).get();

        assertTrue(defaultTimespan.contains(conflictingTimespan));
    }

    @Test
    @DisplayName("return false if doesnt conflict with another timespan")
    public void assertFalseTimespan() {
        ProjectTimespan conflictingTimespan = ProjectTimespan.of(
                Optional.of(new StartDate("2020-01-01")),
                Optional.of(new RelevantEndDate(LocalDate.of(2020, 1, 1)))
        ).get();

        assertFalse(defaultTimespan.contains(conflictingTimespan));
    }


    @Test
    @DisplayName("return true if it conflicts with another timespan on the same start day")
    public void assertFalseTimespanSameStart() {
        ProjectTimespan conflictingTimespan = ProjectTimespan.of(
                startDate,
                Optional.of(new RelevantEndDate(startDate.get().unbox()))
        ).get();

        assertTrue(defaultTimespan.contains(conflictingTimespan));
    }

    @Test
    @DisplayName("return true if it conflicts with another timespan on the same end day")
    public void assertFalseTimespanSameEnd() {
        ProjectTimespan conflictingTimespan = ProjectTimespan.of(
                Optional.of(new StartDate(relevantEndDate.get().unbox())),
                relevantEndDate
        ).get();

        assertTrue(defaultTimespan.contains(conflictingTimespan));
    }
}
