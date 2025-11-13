package org.supplychain.mysupply.production.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.production.dto.ProductDTO;
import org.supplychain.mysupply.production.dto.ProductResponseDTO;

public interface IProductService {

    ProductResponseDTO createProduct(ProductDTO productDTO);

    ProductResponseDTO getProductById(Long id);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable);

    Page<ProductResponseDTO> getLowStockProducts(Pageable pageable);

    ProductResponseDTO updateProduct(Long id, ProductDTO productDTO);

    ProductResponseDTO updateProductStock(Long id, Integer newStock);

    void deleteProduct(Long id);
}