package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.project.*;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Builder
@Value
public class ProjectView {
    ProjectId projectId;
    ProjectName projectName;
    ProjectDescription projectDescription;
    ProjectLead projectLead;
    Customer customer;
    StartDate startDate;
    PlannedEndDate plannedEndDate;
    ActualEndDate actualEndDate;
    Set<TeamMember> teamMember;
}