package org.supplychain.supplychain.service.modelDelivery.interfaces;


import org.springframework.data.domain.Page;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.response.ApiResponse;

import java.util.List;

public interface ICustomerService {
    ApiResponse<CustomerDto> createCustomer(CustomerDto dto);
    ApiResponse<Page<CustomerDto>> getAllCustomers(int page, int size, String filter);
    ApiResponse<CustomerDto> getCustomerById(Long id);
    ApiResponse<CustomerDto> updateCustomer(Long id, CustomerDto dto);
    ApiResponse<Void> deleteCustomer(Long id);

}