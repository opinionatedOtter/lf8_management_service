package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.DetailedException;
import de.szut.lf8_project.common.ErrorDetail;
import lombok.Getter;
import lombok.NonNull;

public class RepositoryException extends DetailedException {

    public RepositoryException(@NonNull ErrorDetail errorDetail) {
        super(errorDetail);
    }
}
