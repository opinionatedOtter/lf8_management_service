package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.project.*;

import java.util.Optional;

public record CreateProjectCommand(
        ProjectName projectName,
        EmployeeId projectLead,
        CustomerId customerId,
        CustomerContactId contactPersonId,
        Optional<ProjectDescription> projectDescription,
        Optional<StartDate> startDate,
        Optional<PlannedEndDate> plannedEndDate
) {
}