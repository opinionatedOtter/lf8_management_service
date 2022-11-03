package de.szut.lf8_project.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum Errorcode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    @Getter
    private HttpStatus httpRepresentation;

    Errorcode(HttpStatus httpRepresentation) {
        this.httpRepresentation = httpRepresentation;
    }
}
