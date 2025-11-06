package org.supplychain.mysupply.approvisionnement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderResponseDTO;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.service.SupplyOrderService;

@RestController
@RequestMapping("/api/supply-orders")
@RequiredArgsConstructor
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;

    @PostMapping
    public ResponseEntity<SupplyOrderResponseDTO> createSupplyOrder(@Valid @RequestBody SupplyOrderDTO supplyOrderDTO) {
        SupplyOrderResponseDTO createdOrder = supplyOrderService.createSupplyOrder(supplyOrderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyOrderResponseDTO> getSupplyOrderById(@PathVariable Long id) {
        SupplyOrderResponseDTO order = supplyOrderService.getSupplyOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<SupplyOrderResponseDTO>> getAllSupplyOrders(Pageable pageable) {
        Page<SupplyOrderResponseDTO> orders = supplyOrderService.getAllSupplyOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<SupplyOrderResponseDTO>> getSupplyOrdersByStatus(
            @PathVariable SupplyOrderStatus status,
            Pageable pageable) {
        Page<SupplyOrderResponseDTO> orders = supplyOrderService.getSupplyOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Page<SupplyOrderResponseDTO>> getSupplyOrdersBySupplier(
            @PathVariable Long supplierId,
            Pageable pageable) {
        Page<SupplyOrderResponseDTO> orders = supplyOrderService.getSupplyOrdersBySupplier(supplierId, pageable);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplyOrderResponseDTO> updateSupplyOrderStatus(
            @PathVariable Long id,
            @RequestParam SupplyOrderStatus status) {
        SupplyOrderResponseDTO updatedOrder = supplyOrderService.updateSupplyOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplyOrder(@PathVariable Long id) {
        supplyOrderService.deleteSupplyOrder(id);
        return ResponseEntity.noContent().build();
    }
}