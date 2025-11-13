package org.supplychain.mysupply.livraison.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.livraison.dto.DeliveryDTO;
import org.supplychain.mysupply.livraison.dto.DeliveryResponseDTO;
import org.supplychain.mysupply.livraison.dto.OrderSummaryDTO;
import org.supplychain.mysupply.livraison.model.CustomerOrder;
import org.supplychain.mysupply.livraison.model.Delivery;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface DeliveryMapper {

    @Mapping(target = "order", source = "customerOrder")
    DeliveryResponseDTO toResponseDTO(Delivery delivery);

    @Mapping(target = "customerOrder", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "actualDeliveryDate", ignore = true)
    @Mapping(target = "trackingNumber", ignore = true)
    Delivery toEntity(DeliveryDTO deliveryDTO);

    @Mapping(target = "idDelivery", ignore = true)
    @Mapping(target = "customerOrder", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "actualDeliveryDate", ignore = true)
    @Mapping(target = "trackingNumber", ignore = true)
    void updateEntityFromDTO(DeliveryDTO deliveryDTO, @MappingTarget Delivery delivery);

    @Mapping(target = "customer", source = "customer")
    OrderSummaryDTO toOrderSummaryDTO(CustomerOrder customerOrder);
}