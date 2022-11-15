package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.project.ProjectId;
import de.szut.lf8_project.domain.project.ProjectName;
import de.szut.lf8_project.domain.project.TeamMember;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Builder
@Value
public class EmployeesOfProjectView {
    @NonNull
    ProjectId projectId;
    @NonNull
    ProjectName projectName;
    @NonNull
    Set<TeamMember> teamMember;
}
