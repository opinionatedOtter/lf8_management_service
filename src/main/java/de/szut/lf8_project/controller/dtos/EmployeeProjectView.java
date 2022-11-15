package de.szut.lf8_project.controller.dtos;

import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Builder
@Value
public class EmployeeProjectView {
    @NonNull
    ProjectId projectId;
    @NonNull
    ProjectName projectName;
    @NonNull
    Optional<StartDate> startDate;
    @NonNull
    Optional<PlannedEndDate> plannedEndDate;
    @NonNull
    Optional<ActualEndDate> actualEndDate;
    @NonNull
    ProjectRole projectRole;
}
