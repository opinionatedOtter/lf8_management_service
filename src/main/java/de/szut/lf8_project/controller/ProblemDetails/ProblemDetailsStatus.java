package de.szut.lf8_project.controller.ProblemDetails;

import de.szut.lf8_project.common.ValueType;
import org.springframework.http.HttpStatus;


public class ProblemDetailsStatus extends ValueType<String> {

    public ProblemDetailsStatus(HttpStatus value) {
        super(String.valueOf(value.value()));
    }
}
