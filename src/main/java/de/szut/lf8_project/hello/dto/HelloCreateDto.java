package de.szut.lf8_project.hello.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class HelloCreateDto {

    @Size(min = 3, message = "at least length of 3")
    private String message;

    @JsonCreator
    public HelloCreateDto(String message) {
        this.message = message;
    }
}
