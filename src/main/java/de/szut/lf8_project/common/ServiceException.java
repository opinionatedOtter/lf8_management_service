package de.szut.lf8_project.common;

import lombok.NonNull;

public class ServiceException extends DetailedException {

    public ServiceException(@NonNull ErrorDetail errorDetail) {
        super(errorDetail);
    }
}
