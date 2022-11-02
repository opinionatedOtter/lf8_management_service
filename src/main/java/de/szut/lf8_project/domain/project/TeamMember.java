package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.Qualification;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class TeamMember {
    @NotNull EmployeeId employeeId;
    @NotNull Qualification qualification;
}
