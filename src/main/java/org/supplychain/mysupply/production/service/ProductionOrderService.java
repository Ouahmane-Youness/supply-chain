package org.supplychain.mysupply.production.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.production.dto.ProductionOrderDTO;
import org.supplychain.mysupply.production.dto.ProductionOrderResponseDTO;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;
import org.supplychain.mysupply.production.mapper.ProductionOrderMapper;
import org.supplychain.mysupply.production.model.BillOfMaterial;
import org.supplychain.mysupply.production.model.Product;
import org.supplychain.mysupply.production.model.ProductionOrder;
import org.supplychain.mysupply.production.repository.BillOfMaterialRepository;
import org.supplychain.mysupply.production.repository.ProductRepository;
import org.supplychain.mysupply.production.repository.ProductionOrderRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionOrderService {

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductRepository productRepository;
    private final BillOfMaterialRepository billOfMaterialRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductionOrderMapper productionOrderMapper;

    public ProductionOrderResponseDTO createProductionOrder(ProductionOrderDTO productionOrderDTO) {
        if (productionOrderRepository.existsByOrderNumber(productionOrderDTO.getOrderNumber())) {
            throw new RuntimeException("Order number already exists: " + productionOrderDTO.getOrderNumber());
        }

        Product product = productRepository.findById(productionOrderDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productionOrderDTO.getProductId()));

        validateMaterialAvailability(product, productionOrderDTO.getQuantity());

        ProductionOrder productionOrder = productionOrderMapper.toEntity(productionOrderDTO);
        productionOrder.setProduct(product);
        productionOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);
        productionOrder.setPriority(productionOrderDTO.getPriority() != null ?
                productionOrderDTO.getPriority() : Priority.STANDARD);

        int totalProductionTime = product.getProductionTime() * productionOrderDTO.getQuantity();
        productionOrder.setEstimatedProductionTimeHours(totalProductionTime);

        ProductionOrder savedOrder = productionOrderRepository.save(productionOrder);
        return productionOrderMapper.toResponseDTO(savedOrder);
    }

    private void validateMaterialAvailability(Product product, Integer orderQuantity) {
        List<BillOfMaterial> requiredMaterials = billOfMaterialRepository.findByProductIdProduct(product.getIdProduct());
        List<String> insufficientMaterials = new ArrayList<>();

        for (BillOfMaterial bom : requiredMaterials) {
            int requiredQuantity = bom.getQuantity() * orderQuantity;
            int availableStock = bom.getMaterial().getStock();

            if (availableStock < requiredQuantity) {
                insufficientMaterials.add(String.format(
                        "%s (need %d, available %d)",
                        bom.getMaterial().getName(),
                        requiredQuantity,
                        availableStock
                ));
            }
        }

        if (!insufficientMaterials.isEmpty()) {
            throw new RuntimeException("Insufficient materials for production: " + String.join(", ", insufficientMaterials));
        }
    }

    @Transactional(readOnly = true)
    public ProductionOrderResponseDTO getProductionOrderById(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production order not found with id: " + id));
        return productionOrderMapper.toResponseDTO(productionOrder);
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable) {
        return productionOrderRepository.findAll(pageable)
                .map(productionOrderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getProductionOrdersByStatus(ProductionOrderStatus status, Pageable pageable) {
        return productionOrderRepository.findByStatus(status, pageable)
                .map(productionOrderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getProductionOrdersByPriority(Priority priority, Pageable pageable) {
        return productionOrderRepository.findByPriority(priority, pageable)
                .map(productionOrderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductionOrderResponseDTO> getProductionOrdersOrderedByPriority(Pageable pageable) {
        return productionOrderRepository.findAllOrderedByPriorityAndDate(pageable)
                .map(productionOrderMapper::toResponseDTO);
    }

    public ProductionOrderResponseDTO startProduction(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production order not found with id: " + id));

        if (productionOrder.getStatus() != ProductionOrderStatus.EN_ATTENTE) {
            throw new RuntimeException("Can only start production for orders in EN_ATTENTE status");
        }

        validateMaterialAvailability(productionOrder.getProduct(), productionOrder.getQuantity());

        consumeMaterials(productionOrder);

        productionOrder.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        productionOrder.setStartDate(LocalDate.now());
        productionOrder.setEstimatedEndDate(
                LocalDate.now().plusDays(calculateEstimatedDays(productionOrder.getEstimatedProductionTimeHours()))
        );

        ProductionOrder updatedOrder = productionOrderRepository.save(productionOrder);
        return productionOrderMapper.toResponseDTO(updatedOrder);
    }

    private void consumeMaterials(ProductionOrder productionOrder) {
        List<BillOfMaterial> requiredMaterials = billOfMaterialRepository
                .findByProductIdProduct(productionOrder.getProduct().getIdProduct());

        for (BillOfMaterial bom : requiredMaterials) {
            RawMaterial material = bom.getMaterial();
            int requiredQuantity = bom.getQuantity() * productionOrder.getQuantity();

            material.setStock(material.getStock() - requiredQuantity);
            rawMaterialRepository.save(material);
        }
    }

    private long calculateEstimatedDays(Integer estimatedHours) {
        return Math.max(1, Math.round(estimatedHours / 8.0));
    }

    public ProductionOrderResponseDTO completeProduction(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production order not found with id: " + id));

        if (productionOrder.getStatus() != ProductionOrderStatus.EN_PRODUCTION) {
            throw new RuntimeException("Can only complete orders that are in production");
        }

        Product product = productionOrder.getProduct();
        product.setStock(product.getStock() + productionOrder.getQuantity());
        productRepository.save(product);

        productionOrder.setStatus(ProductionOrderStatus.TERMINE);
        productionOrder.setActualEndDate(LocalDate.now());

        ProductionOrder updatedOrder = productionOrderRepository.save(productionOrder);
        return productionOrderMapper.toResponseDTO(updatedOrder);
    }

    public ProductionOrderResponseDTO updateProductionOrderStatus(Long id, ProductionOrderStatus newStatus) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production order not found with id: " + id));

        if (newStatus == ProductionOrderStatus.EN_PRODUCTION) {
            return startProduction(id);
        } else if (newStatus == ProductionOrderStatus.TERMINE) {
            return completeProduction(id);
        } else {
            productionOrder.setStatus(newStatus);
            ProductionOrder updatedOrder = productionOrderRepository.save(productionOrder);
            return productionOrderMapper.toResponseDTO(updatedOrder);
        }
    }

    public void deleteProductionOrder(Long id) {
        ProductionOrder productionOrder = productionOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production order not found with id: " + id));

        if (productionOrder.getStatus() == ProductionOrderStatus.EN_PRODUCTION) {
            throw new RuntimeException("Cannot delete production order that is in progress");
        }

        if (productionOrder.getStatus() == ProductionOrderStatus.TERMINE) {
            throw new RuntimeException("Cannot delete completed production order");
        }

        productionOrderRepository.deleteById(id);
    }
}