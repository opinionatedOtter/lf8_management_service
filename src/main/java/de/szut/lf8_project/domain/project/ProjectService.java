package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.application.ApplicationServiceException;
import de.szut.lf8_project.common.ErrorDetail;
import de.szut.lf8_project.common.Errorcode;
import de.szut.lf8_project.common.FailureMessage;
import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.domain.employee.Employee;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project addEmployeeToProject(ProjectRole roleInProject, Project project, Employee employee) throws ServiceException {
        checkQualification(roleInProject, employee);

        List<Project> collidingProjectsOfEmployee = projectRepository.getAllProjectsOfEmployee(employee.getId())
                .stream()
                .filter(otherProject -> areProjectTimespansColliding(otherProject.getProjectTimespan(), project.getProjectTimespan()) && !project.getProjectId().equals(otherProject.getProjectId()))
                .toList();

        if (collidingProjectsOfEmployee.isEmpty()) {
            return addOrUpdateTeamMember(new TeamMember(employee.getId(), roleInProject), project);
        } else {
            throw getExceptionWithProblematicProjectIds(collidingProjectsOfEmployee);
        }
    }

    public List<TeamMember> getUnavailableTeamMember(Project project, Optional<ProjectTimespan> timespan){
        return project.getTeamMembers().stream()
                .filter(teamMember -> projectRepository.getAllProjectsOfEmployee(teamMember.getEmployeeId())
                        .stream()
                        .anyMatch(otherProject -> areProjectTimespansColliding(otherProject.getProjectTimespan(), timespan) && !project.getProjectId().equals(otherProject.getProjectId()))
                ).toList();
    }

    public Project removeProjectMember(final EmployeeId employeeId, Project projectToUpdate) throws ServiceException {
        Optional<TeamMember> memberToRemove = projectToUpdate.getTeamMembers().stream().filter(
                (teamMember ->
                    teamMember.getEmployeeId().equals(employeeId)
                )
        ).findFirst();

        if(memberToRemove.isPresent()) {
            projectToUpdate.getTeamMembers().remove(memberToRemove.get());
            return projectToUpdate;
        } else {
            throw new ServiceException(
                    new ErrorDetail(
                            Errorcode.ENTITY_NOT_FOUND,
                            new FailureMessage("Employee " + employeeId.unbox() + " was not found in the project.")
                    )
            );
        }
    }

    private Project addOrUpdateTeamMember(TeamMember teamMember, Project project) {
        Set<TeamMember> newTeamMembers = project.getTeamMembers().stream()
                .filter(member -> !member.getEmployeeId().equals(teamMember.getEmployeeId()))
                .collect(Collectors.toSet());
        newTeamMembers.add(teamMember);
        return project.toBuilder().teamMembers(newTeamMembers).build();
    }

    private ServiceException getExceptionWithProblematicProjectIds(List<Project> collidingProjectsOfEmployee) {
        String collidingProjectIds = String.join(", ", collidingProjectsOfEmployee
                .stream()
                .map(p -> p.getProjectId().get().toString()).toList()
        );
        return new ServiceException(
                new ErrorDetail(
                        Errorcode.EMPLOYEE_UNAVAILABLE,
                        new FailureMessage("Employee could not be assigned to Project. The following other Projects of this Employee fall into the same timespan: " + collidingProjectIds)));
    }

    private boolean areProjectTimespansColliding(Optional<ProjectTimespan> timespanA, Optional<ProjectTimespan> timespanB) {
        return timespanA.isPresent() && timespanB.isPresent() && timespanA.get().contains(timespanB.get());
    }

    private void checkQualification(ProjectRole roleInProject, Employee employee) throws ServiceException {
        if (!employee.getSkillset().contains(roleInProject)) {
            throw new ServiceException(new ErrorDetail(Errorcode.EMPLOYEE_UNSUITABLE,
                    new FailureMessage("The Employee with the ID " + employee.getId() + " is missing the Qualification: " + roleInProject)));
        }
    }
}
