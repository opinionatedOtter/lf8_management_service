package de.szut.lf8_project.common;

import lombok.NonNull;

public class ServiceException extends Exception {

    @NonNull
    ErrorDetail errorDetail;

    public ServiceException(@NonNull ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }
}
