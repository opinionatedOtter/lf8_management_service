package de.szut.lf8_project.domain;

import de.szut.lf8_project.repository.EmployeeRepoDto;
import lombok.*;

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

    public static Employee fromModel(EmployeeRepoDto employeeModel) {
        return Employee.builder()
                .id(new EmployeeId(employeeModel.getId()))
                .lastName(new LastName(employeeModel.getLastName()))
                .firstName(new FirstName(employeeModel.getFirstName()))
                .street(new Street(employeeModel.getStreet()))
                .postcode(new Postcode(employeeModel.getPostcode()))
                .skillset(employeeModel.getSkillset().stream().map(Qualification::new).toList())
                .build();
    }
}