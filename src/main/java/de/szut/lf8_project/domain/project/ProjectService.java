package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.common.Statuscode;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.repository.RepositoryException;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    // TODO
    public void checkIsValidTeamMember(Project project, Employee employee) throws ServiceException {
        throw new NotYetImplementedException();
    }

    private void checkTeamMemberAvailability(Project project, Employee employee) {

    }

    private void checkTeamMemberQualification(Project project, Employee employee) {

    }
}
