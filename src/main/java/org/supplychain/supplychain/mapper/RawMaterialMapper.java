package org.supplychain.supplychain.mapper;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialRequestDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialResponseDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialSummaryDTO;
import org.supplychain.supplychain.dto.rawmaterial.SupplierSummaryDTO;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.model.Supplier;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    @Mapping(target = "id", source = "idMaterial")
    @Mapping(target = "availableStock", expression = "java(rawMaterial.getStock() - rawMaterial.getReservedStock())")
    @Mapping(target = "isCriticalStock", expression = "java(rawMaterial.getStock() < rawMaterial.getStockMin())")
    RawMaterialResponseDTO toResponseDTO(RawMaterial rawMaterial);

    @Mapping(target = "id", source = "idMaterial")
    @Mapping(target = "availableStock", expression = "java(rawMaterial.getStock() - rawMaterial.getReservedStock())")
    @Mapping(target = "isCriticalStock", expression = "java(rawMaterial.getStock() < rawMaterial.getStockMin())")
    RawMaterialSummaryDTO toSummaryDTO(RawMaterial rawMaterial);

    List<RawMaterialSummaryDTO> toSummaryDTOList(List<RawMaterial> rawMaterials);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "reservedStock", constant = "0")
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    RawMaterial toEntity(RawMaterialRequestDTO requestDTO);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "reservedStock", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDTO(RawMaterialRequestDTO requestDTO, @MappingTarget RawMaterial rawMaterial);

    @Mapping(target = "id", source = "idSupplier")
    SupplierSummaryDTO toSupplierSummaryDTO(Supplier supplier);

    List<SupplierSummaryDTO> toSupplierSummaryDTOList(List<Supplier> suppliers);
}