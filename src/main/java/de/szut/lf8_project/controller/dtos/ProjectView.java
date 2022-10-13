package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.project.*;

public record ProjectView (
    ProjectId projectId,
    ProjectName projectName,
    ProjectDescription projectDescription,
    ProjectLead projectLead,
    Customer customer,
    StartDate startDate,
    PlannedEndDate plannedEndDate,
    ActualEndDate actualEndDate
) {}