package de.szut.lf8_project.domain;

import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.customer.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DisplayName("The CustomerService should")
public class TestCustomerService {

    private final CustomerService customerService = new CustomerService();

    @Test
    @DisplayName("validate a customer")
    public void validateCustomer() {
        CustomerId customerId = new CustomerId(1L);

        Assertions.assertDoesNotThrow(() -> customerService.validateCustomer(customerId));
    }
}
