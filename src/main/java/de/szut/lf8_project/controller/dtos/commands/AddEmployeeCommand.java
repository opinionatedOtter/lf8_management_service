package de.szut.lf8_project.controller.dtos.commands;

import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import lombok.NonNull;
import lombok.Value;

@Value
public class AddEmployeeCommand {
    @NonNull EmployeeId employeeId;
    @NonNull ProjectRole projectRole;
}
