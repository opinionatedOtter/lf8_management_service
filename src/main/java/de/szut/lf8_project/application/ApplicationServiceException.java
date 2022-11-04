package de.szut.lf8_project.application;

import de.szut.lf8_project.common.ErrorDetail;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ApplicationServiceException extends RuntimeException{

    @NonNull
    private ErrorDetail errorDetail;

    public ApplicationServiceException(@NonNull ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }


}
