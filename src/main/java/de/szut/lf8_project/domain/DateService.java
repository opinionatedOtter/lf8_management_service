package de.szut.lf8_project.domain;

import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.domain.project.ActualEndDate;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.Project;
import de.szut.lf8_project.domain.project.StartDate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DateService {

    public void validateProjectStartAndPlannedEnd(Optional<StartDate> startDate, Optional<PlannedEndDate> plannedEndDate) throws ServiceException {
        if (startDate.isPresent() && plannedEndDate.isPresent()) {
            if (startDate.get().unbox().isAfter(plannedEndDate.get().unbox())) {
                throw new ServiceException(new ErrorDetail(Errorcode.END_DATE_BEFORE_START, new FailureMessage("Start date is after the planned end Date.")));
            }
        }
    }

    public void validateProjectDateChange(
            Project project, Optional<StartDate> newStart, Optional<PlannedEndDate> newPlannedEnd, Optional<ActualEndDate> newActualEnd
    ) throws ServiceException {
        validateProjectStartAndPlannedEnd(newStart, newPlannedEnd);
        validateProjectStartAndActualEnd(newStart, newActualEnd);

        validateProjectStartAndPlannedEnd(newStart, project.getPlannedEndDate());
        validateProjectStartAndActualEnd(newStart, project.getActualEndDate());

        validateProjectStartAndPlannedEnd(project.getStartDate(), newPlannedEnd);
        validateProjectStartAndActualEnd(project.getStartDate(), newActualEnd);
    }

    public void validateProjectStartAndActualEnd(Optional<StartDate> startDate, Optional<ActualEndDate> actualEndDate) throws ServiceException {
        if (startDate.isPresent() && actualEndDate.isPresent()) {
            if (startDate.get().unbox().isAfter(actualEndDate.get().unbox())) {
                throw new ServiceException(new ErrorDetail(Errorcode.END_DATE_BEFORE_START, new FailureMessage("Start date is after the actual end Date.")));
            }
        }
    }
}