package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.project.ActualEndDate;
import de.szut.lf8_project.domain.project.PlannedEndDate;
import de.szut.lf8_project.domain.project.ProjectDescription;
import de.szut.lf8_project.domain.project.ProjectId;
import de.szut.lf8_project.domain.project.ProjectLead;
import de.szut.lf8_project.domain.project.ProjectName;
import de.szut.lf8_project.domain.project.StartDate;
import de.szut.lf8_project.domain.project.TeamMember;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

@Builder
@Value
public class ProjectView {
    ProjectId projectId;
    ProjectName projectName;
    Optional<ProjectDescription> projectDescription;
    ProjectLead projectLead;
    Customer customer;
    Optional<StartDate> startDate;
    Optional<PlannedEndDate> plannedEndDate;
    Optional<ActualEndDate> actualEndDate;
    Set<TeamMember> teamMember;
}

