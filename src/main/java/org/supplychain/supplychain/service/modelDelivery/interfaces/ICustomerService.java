package org.supplychain.supplychain.service.modelDelivery.interfaces;


import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;

import java.util.List;

public interface ICustomerService {
    CustomerDto createCustomer(CustomerDto dto);
    List<CustomerDto> getAllCustomers();
    CustomerDto getCustomerById(Long id);
    CustomerDto updateCustomer(Long id, CustomerDto dto);
    void deleteCustomer(Long id);
}