package de.szut.lf8_project.common;

import lombok.NonNull;
import lombok.Value;

@Value
public class ErrorDetail {
    @NonNull
    Errorcode errorCode;
    @NonNull
    FailureMessage failureMessage;
}
