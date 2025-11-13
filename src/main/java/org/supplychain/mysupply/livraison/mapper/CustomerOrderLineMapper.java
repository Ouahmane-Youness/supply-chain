package org.supplychain.mysupply.livraison.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.supplychain.mysupply.livraison.dto.OrderLineDTO;
import org.supplychain.mysupply.livraison.dto.OrderLineResponseDTO;
import org.supplychain.mysupply.livraison.model.CustomerOrderLine;
import org.supplychain.mysupply.production.mapper.ProductMapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CustomerOrderLineMapper {

    OrderLineResponseDTO toResponseDTO(CustomerOrderLine customerOrderLine);

    @Mapping(target = "customerOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    CustomerOrderLine toEntity(OrderLineDTO orderLineDTO);

    default BigDecimal calculateTotalPrice(CustomerOrderLine customerOrderLine) {
        return customerOrderLine.getUnitPrice()
                .multiply(BigDecimal.valueOf(customerOrderLine.getQuantity()));
    }
}