package de.szut.lf8_project.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Employee (
        EmployeeId id,
        LastName lastName,
        FirstName firstName,
        Street street,
        Postcode postcode,
        List<Qualification> skillset
){}
