package org.supplychain.mysupply.approvisionnement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderLineDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderLineResponseDTO;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrderLine;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", uses = {RawMaterialMapper.class})
public interface SupplyOrderLineMapper {

    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(supplyOrderLine))")
    SupplyOrderLineResponseDTO toResponseDTO(SupplyOrderLine supplyOrderLine);

    @Mapping(target = "supplyOrder", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    SupplyOrderLine toEntity(SupplyOrderLineDTO supplyOrderLineDTO);

    default BigDecimal calculateTotalPrice(SupplyOrderLine supplyOrderLine) {
        return supplyOrderLine.getUnitPrice()
                .multiply(BigDecimal.valueOf(supplyOrderLine.getQuantity()));
    }
}