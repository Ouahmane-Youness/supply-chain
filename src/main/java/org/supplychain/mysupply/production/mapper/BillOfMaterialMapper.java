package org.supplychain.mysupply.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.supplychain.mysupply.approvisionnement.mapper.RawMaterialMapper;
import org.supplychain.mysupply.production.dto.BillOfMaterialDTO;
import org.supplychain.mysupply.production.dto.BillOfMaterialResponseDTO;
import org.supplychain.mysupply.production.model.BillOfMaterial;

@Mapper(componentModel = "spring", uses = {RawMaterialMapper.class})
public interface BillOfMaterialMapper {

    @Mapping(target = "materialAvailable", expression = "java(billOfMaterial.getMaterial().getStock() >= billOfMaterial.getQuantity())")
    BillOfMaterialResponseDTO toResponseDTO(BillOfMaterial billOfMaterial);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "material", ignore = true)
    BillOfMaterial toEntity(BillOfMaterialDTO billOfMaterialDTO);
}