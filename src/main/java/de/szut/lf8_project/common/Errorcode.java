package de.szut.lf8_project.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum Errorcode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    @Getter
    private HttpStatus httpRepresentation;

    Errorcode(HttpStatus httpRepresentation) {
        this.httpRepresentation = httpRepresentation;
    }
}
