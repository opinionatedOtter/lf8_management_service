package de.szut.lf8_project.repository;

import de.szut.lf8_project.domain.employee.*;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {


    public Employee dtoToEntity(EmployeeData employeeDto) {
        return Employee.builder()
                .id(new EmployeeId(employeeDto.getId()))
                .lastName(new LastName(employeeDto.getLastName()))
                .firstName(new FirstName(employeeDto.getFirstName()))
                .street(new Street(employeeDto.getStreet()))
                .postcode(new Postcode(employeeDto.getPostcode()))
                .city(new City(employeeDto.getCity()))
                .phonenumber(new Phonenumber(employeeDto.getPhone()))
                .skillset(employeeDto.getSkillSet().stream().map(ProjectRole::new).toList())
                .build();
    }
}
