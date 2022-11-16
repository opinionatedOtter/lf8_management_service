package de.szut.lf8_project.domain.adapter.openApi.schemas;

import lombok.Data;

import java.util.Set;

@Data
public class EmployeesOfProjectViewSchema {
    Long projectId;
    String projectName;
    Set<TeamMemberSchema> teamMember;
}
