package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
public class CreateProjectCommand {
    @NonNull
    ProjectName projectName;
    @NonNull
    ProjectLeadId projectLeadId;
    @NonNull
    CustomerId customerId;
    @NonNull
    CustomerContact customerContact;
    Optional<ProjectDescription> projectDescription;
    Optional<StartDate> startDate;
    Optional<PlannedEndDate> plannedEndDate;

}