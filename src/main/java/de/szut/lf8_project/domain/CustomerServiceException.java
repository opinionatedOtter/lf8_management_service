package de.szut.lf8_project.domain;

import de.szut.lf8_project.common.DetailedException;
import de.szut.lf8_project.common.ErrorDetail;
import lombok.NonNull;

public class CustomerServiceException extends DetailedException {
    public CustomerServiceException(@NonNull ErrorDetail errorDetail) {
        super(errorDetail);
    }
}
