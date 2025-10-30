package org.supplychain.supplychain.mapper.modelDelivery;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    //CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerDto customerDto);
}
