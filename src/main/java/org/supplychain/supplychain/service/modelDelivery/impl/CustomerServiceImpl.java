package org.supplychain.supplychain.service.modelDelivery.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.modelDelivery.CustomerMapper;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.repository.modelDelivery.CustomerRepository;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDto createCustomer(CustomerDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Un client avec cet email existe déjà");
        }
        Customer customer = customerMapper.toEntity(dto);
        customer = customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }


    @Override
    public Page<CustomerDto> getAllCustomers(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customersPage;

        if (filter != null && !filter.isEmpty()) {
            customersPage = customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(filter, filter, pageable);
        } else {
            customersPage = customerRepository.findAll(pageable);
        }

        if (customersPage.isEmpty()) {
            throw new ResourceNotFoundException("Aucun client n'a été trouvé");
        }

        return customersPage.map(customerMapper::toDto);
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID = " + id));
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID = " + id));

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(updatedCustomer);
    }

    //delet customer
    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID = " + id));

        customerRepository.delete(customer);
    }

}
