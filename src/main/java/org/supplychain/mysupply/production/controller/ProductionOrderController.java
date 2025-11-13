package org.supplychain.mysupply.production.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.mysupply.production.dto.ProductionOrderDTO;
import org.supplychain.mysupply.production.dto.ProductionOrderResponseDTO;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;
import org.supplychain.mysupply.production.service.ProductionOrderService;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    @PostMapping
    public ResponseEntity<ProductionOrderResponseDTO> createProductionOrder(@Valid @RequestBody ProductionOrderDTO productionOrderDTO) {
        ProductionOrderResponseDTO createdOrder = productionOrderService.createProductionOrder(productionOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponseDTO> getProductionOrderById(@PathVariable Long id) {
        ProductionOrderResponseDTO order = productionOrderService.getProductionOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getAllProductionOrders(Pageable pageable) {
        Page<ProductionOrderResponseDTO> orders = productionOrderService.getAllProductionOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getProductionOrdersByStatus(
            @PathVariable ProductionOrderStatus status,
            Pageable pageable) {
        Page<ProductionOrderResponseDTO> orders = productionOrderService.getProductionOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getProductionOrdersByPriority(
            @PathVariable Priority priority,
            Pageable pageable) {
        Page<ProductionOrderResponseDTO> orders = productionOrderService.getProductionOrdersByPriority(priority, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/queue")
    public ResponseEntity<Page<ProductionOrderResponseDTO>> getProductionQueue(Pageable pageable) {
        Page<ProductionOrderResponseDTO> orders = productionOrderService.getProductionOrdersOrderedByPriority(pageable);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<ProductionOrderResponseDTO> startProduction(@PathVariable Long id) {
        ProductionOrderResponseDTO updatedOrder = productionOrderService.startProduction(id);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ProductionOrderResponseDTO> completeProduction(@PathVariable Long id) {
        ProductionOrderResponseDTO updatedOrder = productionOrderService.completeProduction(id);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductionOrderResponseDTO> updateProductionOrderStatus(
            @PathVariable Long id,
            @RequestParam ProductionOrderStatus status) {
        ProductionOrderResponseDTO updatedOrder = productionOrderService.updateProductionOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductionOrder(@PathVariable Long id) {
        productionOrderService.deleteProductionOrder(id);
        return ResponseEntity.noContent().build();
    }
}