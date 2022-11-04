package de.szut.lf8_project.domain;

import de.szut.lf8_project.common.ServiceException;
import de.szut.lf8_project.domain.customer.CustomerId;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    public CustomerId validateCustomer(CustomerId customerId) throws ServiceException {
        return customerId;
    }

}
