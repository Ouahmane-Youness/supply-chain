package org.supplychain.supplychain.service.modelDelivery.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.mapper.modelDelivery.CustomerMapper;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.repository.modelDelivery.CustomerRepository;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = customerMapper.toEntity(dto);
        customer = customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerMapper::toDto)  // map chaque Customer en CustomerDto
                .toList();                   // retourne une List<CustomerDto>
    }


    @Override
    public CustomerDto getCustomerById(Long id) {
        return null;
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto dto) {
        return null;
    }

    @Override
    public void deleteCustomer(Long id) {

    }
}