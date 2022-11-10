package de.szut.lf8_project.domain.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class Employee {
    @NonNull EmployeeId id;
    @NonNull LastName lastName;
    @NonNull FirstName firstName;
    @NonNull Street street;
    @NonNull Postcode postcode;
    @NonNull List<ProjectRole> skillset;
}