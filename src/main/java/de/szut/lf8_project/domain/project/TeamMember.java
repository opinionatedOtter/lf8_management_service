package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.Qualification;

import javax.validation.constraints.NotNull;

public class TeamMember {
    @NotNull EmployeeId employeeId;
    @NotNull Qualification qualification;
}
