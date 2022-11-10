package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class TeamMember {
    @NotNull EmployeeId employeeId;
    @NotNull ProjectRole projectRole;
}
