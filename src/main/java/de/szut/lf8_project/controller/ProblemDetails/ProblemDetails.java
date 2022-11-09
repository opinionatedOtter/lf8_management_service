package de.szut.lf8_project.controller.ProblemDetails;

import de.szut.lf8_project.common.ErrorDetail;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ProblemDetails {
    ProblemDetailsTitle title;
    ProblemDetailsDetail detail;
    ProblemDetailsStatus status;

    public static ProblemDetails fromErrorDetail(ErrorDetail errorDetail) {
        return builder()
                .status(new ProblemDetailsStatus(errorDetail.getErrorCode().getHttpRepresentation()))
                .title(new ProblemDetailsTitle(errorDetail.getErrorCode().name()))
                .detail(new ProblemDetailsDetail(errorDetail.getFailureMessage().unbox()))
                .build();
    }
}
