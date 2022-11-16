package de.szut.lf8_project.domain;


import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.StartDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DisplayName("The DateService should")
public class TestDateService {

    private final DateService dateService = new DateService();

    @Test
    @DisplayName("validate start and end and not throw an exception if both are valid")
    public void validateProjectStartAndEnd() {
        StartDate startDate = new StartDate(LocalDate.of(2022, 1, 1));
        PlannedEndDate plannedEndDate = new PlannedEndDate(LocalDate.of(2022, 2, 2));

        Assertions.assertDoesNotThrow(() -> dateService.validateProjectStartAndPlannedEnd(startDate, plannedEndDate));
    }

    @Test
    @DisplayName("validate start and end and throw an exception if planned end is before start")
    public void validateEndBeforeStart() {
        StartDate startDate = new StartDate(LocalDate.of(2022, 2, 1));
        PlannedEndDate plannedEndDate = new PlannedEndDate(LocalDate.of(2022, 1, 1));

        ServiceException result = Assertions.assertThrows(ServiceException.class, () -> dateService.validateProjectStartAndPlannedEnd(startDate, plannedEndDate));

        assertThat(result.getErrorDetail().getErrorCode()).isEqualTo(Errorcode.END_DATE_BEFORE_START);
        assertThat(result.getErrorDetail().getFailureMessage()).isEqualTo(new FailureMessage("Start date is after the planned end Date."));
    }
}
