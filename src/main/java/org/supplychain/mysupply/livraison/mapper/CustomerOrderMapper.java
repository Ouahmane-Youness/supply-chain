package org.supplychain.mysupply.livraison.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.livraison.dto.OrderDTO;
import org.supplychain.mysupply.livraison.dto.OrderResponseDTO;
import org.supplychain.mysupply.livraison.model.CustomerOrder;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, CustomerOrderLineMapper.class, DeliveryMapper.class})
public interface CustomerOrderMapper {

    OrderResponseDTO toResponseDTO(CustomerOrder customerOrder);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    CustomerOrder toEntity(OrderDTO orderDTO);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    void updateEntityFromDTO(OrderDTO orderDTO, @MappingTarget CustomerOrder customerOrder);
}