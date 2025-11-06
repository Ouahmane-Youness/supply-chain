package org.supplychain.mysupply.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.supplychain.mysupply.production.dto.ProductDTO;
import org.supplychain.mysupply.production.dto.ProductResponseDTO;
import org.supplychain.mysupply.production.model.Product;

@Mapper(componentModel = "spring", uses = {BillOfMaterialMapper.class})
public interface ProductMapper {

    @Mapping(target = "lowStock", expression = "java(product.getStock() <= product.getMinimumStock())")
    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "idProduct", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    void updateEntityFromDTO(ProductDTO productDTO, @MappingTarget Product product);
}