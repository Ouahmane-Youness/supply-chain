package org.supplychain.supplychain.mapper.Production;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.supplychain.supplychain.dto.product.ProductDTO;
import org.supplychain.supplychain.model.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {BillOfMaterialMapper.class}
)
public interface ProductMapper {

    @Mapping(target = "idProduct", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "stock", defaultValue = "0")
    @Mapping(target = "minimumStock", defaultValue = "0")
    Product toEntity(ProductDTO productDTO);


    @Mapping(source = "billOfMaterials", target = "billOfMaterials")
    ProductDTO toDTO(Product product);


    @Mapping(target = "idProduct", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDTO(ProductDTO productDTO, @MappingTarget Product product);
}