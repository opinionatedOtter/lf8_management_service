package de.szut.lf8_project.common;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class DetailedException extends Exception {

    @NonNull
    ErrorDetail errorDetail;

    public DetailedException(@NonNull ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }
}
