package org.supplychain.mysupply.approvisionnement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderResponseDTO;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrder;

@Mapper(componentModel = "spring", uses = {SupplierMapper.class, SupplyOrderLineMapper.class})
public interface SupplyOrderMapper {

    SupplyOrderResponseDTO toResponseDTO(SupplyOrder supplyOrder);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    SupplyOrder toEntity(SupplyOrderDTO supplyOrderDTO);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    void updateEntityFromDTO(SupplyOrderDTO supplyOrderDTO, @MappingTarget SupplyOrder supplyOrder);
}