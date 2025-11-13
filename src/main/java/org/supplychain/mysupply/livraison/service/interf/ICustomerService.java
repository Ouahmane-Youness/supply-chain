package org.supplychain.mysupply.livraison.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.livraison.dto.CustomerDTO;
import org.supplychain.mysupply.livraison.dto.CustomerResponseDTO;

public interface ICustomerService {

    CustomerResponseDTO createCustomer(CustomerDTO customerDTO);

    CustomerResponseDTO getCustomerById(Long id);

    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable);

    Page<CustomerResponseDTO> searchCustomers(String searchTerm, Pageable pageable);

    CustomerResponseDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);

    CustomerResponseDTO getCustomerByEmail(String email);
}