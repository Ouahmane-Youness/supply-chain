package org.supplychain.supplychain.mapper;

import org.supplychain.supplychain.dto.product.ProductDTO;
import org.supplychain.supplychain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setProductionTime(productDTO.getProductionTime());
        product.setCost(productDTO.getCost());
        product.setStock(productDTO.getStock());
        product.setMinimumStock(productDTO.getMinimumStock());
        product.setUnit(productDTO.getUnit());

        return product;
    }

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setProductionTime(product.getProductionTime());
        dto.setCost(product.getCost());
        dto.setStock(product.getStock());
        dto.setMinimumStock(product.getMinimumStock());
        dto.setUnit(product.getUnit());

        return dto;
    }

    public void updateEntityFromDTO(ProductDTO productDTO, Product product) {
        if (productDTO == null || product == null) {
            return;
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setProductionTime(productDTO.getProductionTime());
        product.setCost(productDTO.getCost());
        product.setStock(productDTO.getStock());
        product.setMinimumStock(productDTO.getMinimumStock());
        product.setUnit(productDTO.getUnit());
    }
}