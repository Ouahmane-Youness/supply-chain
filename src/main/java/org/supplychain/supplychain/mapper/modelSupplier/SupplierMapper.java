package org.supplychain.supplychain.mapper.modelSupplier;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.supplier.SupplierDTO;
import org.supplychain.supplychain.model.Supplier;
import java.util.ArrayList; // Keep this import
import java.util.stream.Collectors; // Keep this import

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    @Mapping(target = "materialIds",
            expression = "java(supplier.getMaterials() != null ? supplier.getMaterials().stream().map(m -> m.getIdMaterial()).collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>())")
    SupplierDTO toDTO(Supplier supplier);

    @InheritInverseConfiguration
    @Mapping(target = "supplyOrders", ignore = true)
    @Mapping(target = "materials", ignore = true)
    Supplier toEntity(SupplierDTO dto);
}