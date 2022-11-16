package de.szut.lf8_project.domain.adapter.openApi.schemas;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeProjectViewSchema {

    Long projectId;
    String projectName;
    LocalDate startDate;
    LocalDate plannedEndDate;
    LocalDate actualEndDate;
    String projectRole;
}
