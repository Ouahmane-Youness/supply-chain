package org.supplychain.mysupply.approvisionnement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    @Mapping(target = "lowStock", expression = "java(rawMaterial.getStock() <= rawMaterial.getStockMin())")
    RawMaterialResponseDTO toResponseDTO(RawMaterial rawMaterial);

    @Mapping(target = "lastRestockDate", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    RawMaterial toEntity(RawMaterialDTO rawMaterialDTO);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "lastRestockDate", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    void updateEntityFromDTO(RawMaterialDTO rawMaterialDTO, @MappingTarget RawMaterial rawMaterial);
}