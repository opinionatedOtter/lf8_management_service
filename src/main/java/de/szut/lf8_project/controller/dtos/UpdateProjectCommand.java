package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.project.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Value
@AllArgsConstructor
public class UpdateProjectCommand {
    @NotNull
    Optional<ProjectName> projectName;
    @NotNull
    Optional<ProjectLeadId> projectLeadId;
    @NotNull
    Optional<CustomerId> customerId;
    @NotNull
    Optional<CustomerContact> customerContact;

    @NonNull
    Optional<ProjectDescription> projectDescription;
    @NonNull
    Optional<StartDate> startDate;
    @NonNull
    Optional<PlannedEndDate> plannedEndDate;
    @NonNull
    Optional<ActualEndDate> actualEndDate;
}
