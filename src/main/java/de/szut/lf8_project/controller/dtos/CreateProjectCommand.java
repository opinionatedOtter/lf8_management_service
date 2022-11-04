package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
@AllArgsConstructor
public class CreateProjectCommand {
    @NonNull
    ProjectName projectName;
    @NonNull
    ProjectLeadId projectLeadId;
    @NonNull
    CustomerId customerId;
    @NonNull
    CustomerContact customerContact;
    @NonNull
    Optional<ProjectDescription> projectDescription;
    @NonNull
    Optional<StartDate> startDate;
    @NonNull
    Optional<PlannedEndDate> plannedEndDate;

}