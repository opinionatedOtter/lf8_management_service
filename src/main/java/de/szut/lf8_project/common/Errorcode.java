package de.szut.lf8_project.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum Errorcode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    END_DATE_BEFORE_START(HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST),
    EMPLOYEE_UNAVAILABLE(HttpStatus.BAD_REQUEST);

    @Getter
    private HttpStatus httpRepresentation;

    Errorcode(HttpStatus httpRepresentation) {
        this.httpRepresentation = httpRepresentation;
    }
}
