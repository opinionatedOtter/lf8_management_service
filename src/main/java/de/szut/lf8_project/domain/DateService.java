package de.szut.lf8_project.domain;

import de.szut.lf8_project.common.*;
import de.szut.lf8_project.domain.project.ActualEndDate;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.StartDate;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class DateService {

    public void validateProjectStartAndPlannedEnd(StartDate startDate, PlannedEndDate plannedEndDate) throws ServiceException {
        if (startDate.unbox().isAfter(plannedEndDate.unbox())) {
            throw new ServiceException(new ErrorDetail(Errorcode.END_DATE_BEFORE_START, new FailureMessage("Start date is after the planned end Date.")));
        }
    }

    public void validateProjectDateChange(
            Project project, Optional<StartDate> newStart, Optional<PlannedEndDate> newPlannedEnd, Optional<ActualEndDate> newActualEnd
    ) throws ServiceException {
        // ich nehme gerne Vorschläge an, das hier schöner zu machen ...
        if (newStart.isPresent() && newPlannedEnd.isPresent() && newActualEnd.isPresent()) {
            validateProjectStartAndPlannedEnd(newStart.get(), newPlannedEnd.get());
            validateProjectStartAndActualEnd(newStart.get(), newActualEnd.get());

        } else if (newStart.isPresent() && newActualEnd.isPresent()) {
            validateProjectStartAndActualEnd(newStart.get(), newActualEnd.get());
            if (project.getPlannedEndDate().isPresent()) {
                validateProjectStartAndPlannedEnd(newStart.get(), project.getPlannedEndDate().get());
            }

        } else if (newStart.isPresent() && newPlannedEnd.isPresent()) {
            validateProjectStartAndPlannedEnd(newStart.get(), newPlannedEnd.get());
            if (project.getActualEndDate().isPresent()) {
                validateProjectStartAndActualEnd(newStart.get(), project.getActualEndDate().get());
            }

        } else if (newStart.isPresent()) {
            if (project.getPlannedEndDate().isPresent()) {
                validateProjectStartAndPlannedEnd(newStart.get(), project.getPlannedEndDate().get());
            }
            if (project.getActualEndDate().isPresent()) {
                validateProjectStartAndActualEnd(newStart.get(), project.getActualEndDate().get());
            }

        } else if (newActualEnd.isPresent()) {
            if (project.getStartDate().isPresent()) {
                validateProjectStartAndActualEnd(project.getStartDate().get(), newActualEnd.get());
            }

        } else if (newPlannedEnd.isPresent()) {
            if (project.getStartDate().isPresent()) {
                validateProjectStartAndPlannedEnd(project.getStartDate().get(), newPlannedEnd.get());
            }
        }
    }

    public void validateProjectStartAndActualEnd(StartDate startDate, ActualEndDate actualEndDate) throws ServiceException {
        if (startDate.unbox().isAfter(actualEndDate.unbox())) {
            throw new ServiceException(new ErrorDetail(Errorcode.END_DATE_BEFORE_START, new FailureMessage("Start date is after the actual end Date.")));
        }
    }
}