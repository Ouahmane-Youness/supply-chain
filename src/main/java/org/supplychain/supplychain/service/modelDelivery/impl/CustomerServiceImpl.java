package org.supplychain.supplychain.service.modelDelivery.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.mapper.modelDelivery.CustomerMapper;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.repository.modelDelivery.CustomerRepository;
import org.supplychain.supplychain.response.ApiResponse;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;
import org.springframework.data.domain.Page;


import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public ApiResponse<CustomerDto> createCustomer(CustomerDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            return ApiResponse.<CustomerDto>builder()
                    .message("Un client avec cet email existe deje")
                    .status(HttpStatus.CONFLICT)
                    .data(null)
                    .build();
        }
        Customer customer = customerMapper.toEntity(dto);
        customer = customerRepository.save(customer);

        return ApiResponse.<CustomerDto>builder()
                .message("Client cree avec succes...")
                .status(HttpStatus.CREATED)
                .data(customerMapper.toDto(customer))
                .build();
    }

    @Override
    public ApiResponse<Page<CustomerDto>> getAllCustomers(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Customer> customersPage;

        if (filter != null && !filter.isEmpty()) {
            customersPage = customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(filter, filter, pageable);
        } else {
            customersPage = customerRepository.findAll(pageable);
        }

        if(customersPage.isEmpty()){
            return ApiResponse.<Page<CustomerDto>>builder()
                    .message("Aucun client n'a ete trouve")
                    .status(HttpStatus.NOT_FOUND)
                    .data(Page.empty())
                    .build();
        }

        Page<CustomerDto> customerDtoPage = customersPage.map(customer -> customerMapper.toDto(customer));
        return ApiResponse.<Page<CustomerDto>>builder()
                .message("Liste des clients récupérée avec succès")
                .status(HttpStatus.OK)
                .data(customerDtoPage)
                .build();
    }



    @Override
    public ApiResponse<CustomerDto> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customer -> ApiResponse.<CustomerDto>builder()
                        .message("Client trouvé avec succès")
                        .status(HttpStatus.OK)
                        .data(customerMapper.toDto(customer))
                        .build())
                .orElseGet(() -> ApiResponse.<CustomerDto>builder()
                        .message("Client non trouvé")
                        .status(HttpStatus.NOT_FOUND)
                        .data(null)
                        .build());
    }

    @Override
    public ApiResponse<CustomerDto> updateCustomer(Long id, CustomerDto dto) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(dto.getName());
                    existingCustomer.setEmail(dto.getEmail());
                    existingCustomer.setPhone(dto.getPhone());
                    existingCustomer.setAddress(dto.getAddress());
                    Customer updatedCustomer = customerRepository.save(existingCustomer);
                    return ApiResponse.<CustomerDto>builder()
                            .message("Client mis à jour avec succès")
                            .status(HttpStatus.OK)
                            .data(customerMapper.toDto(updatedCustomer))
                            .build();
                })
                .orElseGet(() -> ApiResponse.<CustomerDto>builder()
                        .message("Client non trouvé")
                        .status(HttpStatus.NOT_FOUND)
                        .data(null)
                        .build());
    }


    @Override
    public ApiResponse<Void> deleteCustomer(Long id) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    customerRepository.delete(existingCustomer);
                    return ApiResponse.<Void>builder()
                            .message("Client supprimé avec succès")
                            .status(HttpStatus.OK)
                            .data(null)
                            .build();
                })
                .orElseGet(() -> ApiResponse.<Void>builder()
                        .message("Client non trouvé")
                        .status(HttpStatus.NOT_FOUND)
                        .data(null)
                        .build());
    }
}