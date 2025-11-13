package org.supplychain.mysupply.livraison.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;
import org.supplychain.mysupply.livraison.dto.CustomerDTO;
import org.supplychain.mysupply.livraison.dto.CustomerResponseDTO;
import org.supplychain.mysupply.livraison.mapper.CustomerMapper;
import org.supplychain.mysupply.livraison.model.Customer;
import org.supplychain.mysupply.livraison.repository.CustomerRepository;
import org.supplychain.mysupply.livraison.service.interf.ICustomerService;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponseDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customerDTO.getEmail());
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDTO(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomers(String searchTerm, Pageable pageable) {
        return customerRepository.findByNameOrEmailContaining(searchTerm, pageable)
                .map(customerMapper::toResponseDTO);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (!customer.getEmail().equals(customerDTO.getEmail()) &&
                customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customerDTO.getEmail());
        }

        customerMapper.updateEntityFromDTO(customerDTO, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }

        long activeOrdersCount = customerRepository.countActiveOrdersByCustomerId(id);
        if (activeOrdersCount > 0) {
            throw new UnauthorizedException("Cannot delete customer with active orders");
        }

        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        return customerMapper.toResponseDTO(customer);
    }
}