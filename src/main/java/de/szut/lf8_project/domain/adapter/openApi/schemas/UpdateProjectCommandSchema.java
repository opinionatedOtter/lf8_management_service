package de.szut.lf8_project.domain.adapter.openApi.schemas;

import lombok.Data;

import java.time.LocalDate;


@Data
public class UpdateProjectCommandSchema {

    String projectName;
    Long projectLeadId;
    Long customerId;
    String customerContact;
    String projectDescription;
    LocalDate startDate;
    LocalDate plannedEndDate;
    LocalDate actualEndDate;


}
