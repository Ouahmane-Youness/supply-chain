package org.supplychain.mysupply.livraison.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.mysupply.livraison.dto.OrderDTO;
import org.supplychain.mysupply.livraison.dto.OrderResponseDTO;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;
import org.supplychain.mysupply.livraison.service.CustomerOrderService;

@RestController
@RequestMapping("/api/customer-orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OrderResponseDTO createdOrder = customerOrderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = customerOrderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(Pageable pageable) {
        Page<OrderResponseDTO> orders = customerOrderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(
            @PathVariable CustomerOrderStatus status,
            Pageable pageable) {
        Page<OrderResponseDTO> orders = customerOrderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByCustomer(
            @PathVariable Long customerId,
            Pageable pageable) {
        Page<OrderResponseDTO> orders = customerOrderService.getOrdersByCustomer(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/without-delivery")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersWithoutDelivery(Pageable pageable) {
        Page<OrderResponseDTO> orders = customerOrderService.getOrdersWithoutDelivery(pageable);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam CustomerOrderStatus status) {
        OrderResponseDTO updatedOrder = customerOrderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        customerOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}