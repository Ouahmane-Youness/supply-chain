package org.supplychain.mysupply.livraison.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.livraison.dto.CustomerDTO;
import org.supplychain.mysupply.livraison.dto.CustomerResponseDTO;
import org.supplychain.mysupply.livraison.mapper.CustomerMapper;
import org.supplychain.mysupply.livraison.model.Customer;
import org.supplychain.mysupply.livraison.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponseDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + customerDTO.getEmail());
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDTO(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return customerMapper.toResponseDTO(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(customerMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomers(String searchTerm, Pageable pageable) {
        return customerRepository.findByNameOrEmailContaining(searchTerm, pageable)
                .map(customerMapper::toResponseDTO);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (!customer.getEmail().equals(customerDTO.getEmail()) &&
                customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + customerDTO.getEmail());
        }

        customerMapper.updateEntityFromDTO(customerDTO, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }

        long activeOrdersCount = customerRepository.countActiveOrdersByCustomerId(id);
        if (activeOrdersCount > 0) {
            throw new RuntimeException("Cannot delete customer with active orders");
        }

        customerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
        return customerMapper.toResponseDTO(customer);
    }
}