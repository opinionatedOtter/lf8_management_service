package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.ErrorDetail;
import lombok.NonNull;

public class RepositoryException extends Exception {

    @NonNull
    ErrorDetail errorDetail;

    public RepositoryException(@NonNull ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }
}
