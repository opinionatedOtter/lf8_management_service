package de.szut.lf8_project.hello;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
public class HelloCreateDto {

    @Size(min = 3, message = "at least length of 3")
    private String message;
}
