package de.szut.lf8_project.domain;

import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.StartDate;
import org.springframework.stereotype.Service;

@Service
public class DateService {

    public void validateProjectStartAndEnd(StartDate startDate, PlannedEndDate plannedEndDate) throws ServiceException {
        if (startDate.unbox().isAfter(plannedEndDate.unbox())) {
            throw new ServiceException(new ErrorDetail(Errorcode.END_DATE_BEFORE_START, new FailureMessage("Start date is after the planned end Date.")));
        }
    }
}