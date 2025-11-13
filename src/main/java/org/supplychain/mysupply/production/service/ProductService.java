package org.supplychain.mysupply.production.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;
import org.supplychain.mysupply.production.dto.BillOfMaterialDTO;
import org.supplychain.mysupply.production.dto.ProductDTO;
import org.supplychain.mysupply.production.dto.ProductResponseDTO;
import org.supplychain.mysupply.production.mapper.ProductMapper;
import org.supplychain.mysupply.production.model.BillOfMaterial;
import org.supplychain.mysupply.production.model.Product;
import org.supplychain.mysupply.production.repository.BillOfMaterialRepository;
import org.supplychain.mysupply.production.repository.ProductRepository;
import org.supplychain.mysupply.production.service.interf.IProductService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO createProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.getName())) {
            throw new IllegalArgumentException("Product name already exists: " + productDTO.getName());
        }

        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);

        if (productDTO.getBillOfMaterials() != null) {
            createBillOfMaterials(savedProduct, productDTO.getBillOfMaterials());
        }

        return productMapper.toResponseDTO(savedProduct);
    }

    private void createBillOfMaterials(Product product, List<BillOfMaterialDTO> bomDTOs) {
        List<BillOfMaterial> billOfMaterials = new ArrayList<>();

        for (BillOfMaterialDTO bomDTO : bomDTOs) {
            RawMaterial material = rawMaterialRepository.findById(bomDTO.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Raw material not found with id: " + bomDTO.getMaterialId()));

            if (billOfMaterialRepository.existsByProductIdProductAndMaterialIdMaterial(
                    product.getIdProduct(), material.getIdMaterial())) {
                throw new IllegalArgumentException("Material already exists in BOM for this product");
            }

            BillOfMaterial bom = new BillOfMaterial();
            bom.setProduct(product);
            bom.setMaterial(material);
            bom.setQuantity(bomDTO.getQuantity());

            billOfMaterials.add(bom);
        }

        billOfMaterialRepository.saveAll(billOfMaterials);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> searchProducts(String searchTerm, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> getLowStockProducts(Pageable pageable) {
        return productRepository.findLowStockProducts(pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getName().equals(productDTO.getName()) &&
                productRepository.existsByName(productDTO.getName())) {
            throw new IllegalArgumentException("Product name already exists: " + productDTO.getName());
        }

        productMapper.updateEntityFromDTO(productDTO, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    public ProductResponseDTO updateProductStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        long productionOrdersCount = productRepository.countProductionOrdersByProductId(id);
        if (productionOrdersCount > 0) {
            throw new UnauthorizedException("Cannot delete product with existing production orders");
        }

        productRepository.deleteById(id);
    }
}