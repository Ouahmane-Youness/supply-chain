package org.supplychain.mysupply.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.production.dto.ProductionOrderDTO;
import org.supplychain.mysupply.production.dto.ProductionOrderResponseDTO;
import org.supplychain.mysupply.production.model.ProductionOrder;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ProductionOrderMapper {

    ProductionOrderResponseDTO toResponseDTO(ProductionOrder productionOrder);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "estimatedEndDate", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "estimatedProductionTimeHours", ignore = true)
    ProductionOrder toEntity(ProductionOrderDTO productionOrderDTO);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "estimatedEndDate", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "estimatedProductionTimeHours", ignore = true)
    void updateEntityFromDTO(ProductionOrderDTO productionOrderDTO, @MappingTarget ProductionOrder productionOrder);
}