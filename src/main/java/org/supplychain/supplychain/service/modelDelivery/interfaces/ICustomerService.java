package org.supplychain.supplychain.service.modelDelivery.interfaces;


import org.springframework.data.domain.Page;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;

public interface ICustomerService {
    CustomerDto createCustomer(CustomerDto dto);
    Page<CustomerDto> getAllCustomers(int page, int size, String filter);
    CustomerDto getCustomerById(Long id);
    CustomerDto updateCustomer(Long id, CustomerDto dto);
    void deleteCustomer(Long id);
}