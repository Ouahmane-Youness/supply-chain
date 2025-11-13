package org.supplychain.mysupply.production.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.production.dto.ProductionOrderDTO;
import org.supplychain.mysupply.production.dto.ProductionOrderResponseDTO;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;

public interface IProductionOrderService {

    ProductionOrderResponseDTO createProductionOrder(ProductionOrderDTO productionOrderDTO);

    ProductionOrderResponseDTO getProductionOrderById(Long id);

    Page<ProductionOrderResponseDTO> getAllProductionOrders(Pageable pageable);

    Page<ProductionOrderResponseDTO> getProductionOrdersByStatus(ProductionOrderStatus status, Pageable pageable);

    Page<ProductionOrderResponseDTO> getProductionOrdersByPriority(Priority priority, Pageable pageable);

    Page<ProductionOrderResponseDTO> getProductionOrdersOrderedByPriority(Pageable pageable);

    ProductionOrderResponseDTO startProduction(Long id);

    ProductionOrderResponseDTO completeProduction(Long id);

    ProductionOrderResponseDTO updateProductionOrderStatus(Long id, ProductionOrderStatus newStatus);

    void deleteProductionOrder(Long id);
}