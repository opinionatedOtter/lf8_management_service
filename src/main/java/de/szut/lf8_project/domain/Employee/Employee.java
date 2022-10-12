package de.szut.lf8_project.domain.Employee;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;

public record Employee(@NonNull EmployeeId id,
                       @NonNull LastName lastName,
                       @NonNull FirstName firstName,
                       @NonNull Street street,
                       @NonNull Postcode postcode,
                       @NonNull List<Qualification> skillset) {

    @Builder
    public Employee {
    }
}