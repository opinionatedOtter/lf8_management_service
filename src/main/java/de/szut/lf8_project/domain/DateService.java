package de.szut.lf8_project.domain;

import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.StartDate;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

@Service
public class DateService {

    // TODO
    public void validateProjectStartAndEnd(StartDate startDate, PlannedEndDate plannedEndDate) throws ServiceException {
        throw new NotYetImplementedException();
    }
}
