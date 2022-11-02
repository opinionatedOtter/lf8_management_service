package de.szut.lf8_project.domain.project;

import de.szut.lf8_project.controller.dtos.CreateProjectDto;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@Builder
@Value
public class Project {
    @NonNull
    Optional<ProjectId> projectId;
    @NonNull
    ProjectName projectName;
    @NonNull
    ProjectDescription projectDescription;
    @NonNull
    ProjectLead projectLead;
    @NonNull
    Customer customer;
    @NonNull
    StartDate startDate;
    @NonNull
    PlannedEndDate plannedEndDate;
    @NonNull
    Optional<ActualEndDate> actualEndDate;
    @NonNull
    Set<TeamMember> teamMembers;

    public static Project fromDto(CreateProjectDto dto) {
        // TODO: MS-2 discuss - selbst validieren in Valuetypes?
        return builder()
                .projectId(Optional.empty())
                .projectName(new ProjectName(dto.projectName()))
                .projectDescription(new ProjectDescription(dto.projectDescription()))
                .projectLead(new ProjectLead(new ProjectLeadId(dto.projectLead())))
                .customer(new Customer(new CustomerId(dto.customerId())))
                .startDate(new StartDate(dto.startDate()))
                .plannedEndDate(new PlannedEndDate(dto.plannedEndDate()))
                .actualEndDate(Optional.empty())
                .teamMembers(Collections.emptySet())
                .build();
    }
}