package org.supplychain.supplychain.service.Production.Product;



import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.product.ProductDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.ProductMapper;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.repository.Production.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private  final ProductMapper productMapper;



    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        productRepository.findByName(productDTO.getName())
                .ifPresent(existingProduct -> {
                    throw new DuplicateResourceException("Un produit avec le nom '" + productDTO.getName() + "' existe déjà");
                });

        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }


    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        if (productRepository.existsByNameAndIdProductNot(productDTO.getName(), id)) {
            throw new DuplicateResourceException("Un autre produit avec le nom '" + productDTO.getName() + "' existe déjà");
        }

        productMapper.updateEntityFromDTO(productDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        // Vérifier s'il existe des ordres de production associés
        Long productionOrderCount = productRepository.countProductionOrdersByProductId(id);
        if (productionOrderCount > 0) {
            throw new ResourceInUseException(
                    "Impossible de supprimer le produit. Il existe " + productionOrderCount +
                            " ordre(s) de production associé(s) à ce produit."
            );
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));
        return productMapper.toDTO(product);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }


}
