package de.szut.lf8_project.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Employee (
        @JsonProperty("id") EmployeeId id,
        @JsonProperty("lastName") LastName lastName,
        @JsonProperty("firstName") FirstName firstName,
        @JsonProperty("street") Street street,
        @JsonProperty("postcode") Postcode postcode,
        @JsonProperty("skillset") List<Qualification> skillset
){}
