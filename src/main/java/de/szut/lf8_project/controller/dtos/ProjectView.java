package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.project.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

@Builder
@Value
public class ProjectView {
    @NonNull
    ProjectId projectId;
    @NonNull
    ProjectName projectName;
    @NonNull
    Optional<ProjectDescription> projectDescription;
    @NonNull
    ProjectLead projectLead;
    @NonNull
    Customer customer;
    @NonNull
    CustomerContact customerContact;
    @NonNull
    Optional<StartDate> startDate;
    @NonNull
    Optional<PlannedEndDate> plannedEndDate;
    @NonNull
    Optional<ActualEndDate> actualEndDate;
    @NonNull
    Set<TeamMember> teamMember;
}

