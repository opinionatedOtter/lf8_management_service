package de.szut.lf8_project.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Jacksonized
@Value
public class EmployeeData {
    @JsonProperty("id")
    Long id;
    @JsonProperty("lastName")
    String lastName;
    @JsonProperty("firstName")
    String firstName;
    @JsonProperty("street")
    String street;
    @JsonProperty("postcode")
    String postcode;
    @JsonProperty("city")
    String city;
    @JsonProperty("phone")
    String phone;
    @JsonProperty("skillSet")
    @Builder.Default
    List<String> skillSet = Collections.emptyList();
}
