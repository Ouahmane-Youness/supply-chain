package org.supplychain.mysupply.approvisionnement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.approvisionnement.dto.SupplierDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplierResponseDTO;
import org.supplychain.mysupply.approvisionnement.model.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierResponseDTO toResponseDTO(Supplier supplier);

    Supplier toEntity(SupplierDTO supplierDTO);

    @Mapping(target = "idSupplier", ignore = true)
    @Mapping(target = "supplyOrders", ignore = true)
    @Mapping(target = "materials", ignore = true)
    void updateEntityFromDTO(SupplierDTO supplierDTO, @MappingTarget Supplier supplier);
}