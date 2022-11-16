package de.szut.lf8_project.domain.adapter.openApi.schemas;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectViewSchema {
    Long projectId;
    String projectName;
    String projectDescription;
    ProjectLeadSchema projectLead;
    CustomerSchema customer;
    String customerContact;
    LocalDate startDate;
    LocalDate plannedEndDate;
    LocalDate actualEndDate;
    Set<TeamMemberSchema> teamMember;
}

