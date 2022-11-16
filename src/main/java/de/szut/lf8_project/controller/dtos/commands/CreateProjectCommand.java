package de.szut.lf8_project.controller.dtos.commands;

import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Value
@Builder
@AllArgsConstructor
public class CreateProjectCommand {
    @NotNull
    ProjectName projectName;
    @NotNull
    ProjectLeadId projectLeadId;
    @NotNull
    CustomerId customerId;
    @NotNull
    CustomerContact customerContact;

    @NonNull
    Optional<ProjectDescription> projectDescription;
    @NonNull
    Optional<StartDate> startDate;
    @NonNull
    Optional<PlannedEndDate> plannedEndDate;

}