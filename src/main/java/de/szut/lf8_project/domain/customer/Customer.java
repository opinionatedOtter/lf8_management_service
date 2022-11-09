package de.szut.lf8_project.domain.customer;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class Customer {
    @NotNull CustomerId customerId;
}
