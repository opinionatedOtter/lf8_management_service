package de.szut.lf8_project.domain.customer;

import javax.validation.constraints.NotNull;

public record Customer(
        @NotNull CustomerId customerId
        ) {
}
