package org.supplychain.mysupply.livraison.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.livraison.dto.CustomerDTO;
import org.supplychain.mysupply.livraison.dto.CustomerResponseDTO;
import org.supplychain.mysupply.livraison.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "totalOrders", expression = "java(customer.getCustomerOrders().size())")
    CustomerResponseDTO toResponseDTO(Customer customer);

    @Mapping(target = "customerOrders", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);

    @Mapping(target = "idCustomer", ignore = true)
    @Mapping(target = "customerOrders", ignore = true)
    void updateEntityFromDTO(CustomerDTO customerDTO, @MappingTarget Customer customer);
}