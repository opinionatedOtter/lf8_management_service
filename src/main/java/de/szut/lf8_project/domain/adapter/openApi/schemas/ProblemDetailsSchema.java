package de.szut.lf8_project.domain.adapter.openApi.schemas;

import lombok.Data;

@Data
public class ProblemDetailsSchema {
    String title;
    String detail;
    Integer status;
}
