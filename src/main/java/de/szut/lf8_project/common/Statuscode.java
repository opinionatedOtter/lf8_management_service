package de.szut.lf8_project.common;

import lombok.Getter;

import java.util.Arrays;

public enum Statuscode {
    UNAUTHORIZED("401"),
    NOT_FOUND("404"),
    INTERNAL_SERVER_ERROR("500");

    private String value;

    Statuscode(String value) {
        this.value = value;
    }



    public static Statuscode of(String statuscode) {
       return Arrays.stream(Statuscode.values())
                .filter(statuscodeEnum -> statuscode.equals(statuscodeEnum.value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
