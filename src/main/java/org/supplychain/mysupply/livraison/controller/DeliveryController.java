package org.supplychain.mysupply.livraison.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.mysupply.livraison.dto.DeliveryDTO;
import org.supplychain.mysupply.livraison.dto.DeliveryResponseDTO;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;
import org.supplychain.mysupply.livraison.service.DeliveryService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@Valid @RequestBody DeliveryDTO deliveryDTO) {
        DeliveryResponseDTO createdDelivery = deliveryService.createDelivery(deliveryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDelivery);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryById(@PathVariable Long id) {
        DeliveryResponseDTO delivery = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDTO>> getAllDeliveries(Pageable pageable) {
        Page<DeliveryResponseDTO> deliveries = deliveryService.getAllDeliveries(pageable);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<DeliveryResponseDTO>> getDeliveriesByStatus(
            @PathVariable DeliveryStatus status,
            Pageable pageable) {
        Page<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesByStatus(status, pageable);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/driver/{driver}")
    public ResponseEntity<Page<DeliveryResponseDTO>> getDeliveriesByDriver(
            @PathVariable String driver,
            Pageable pageable) {
        Page<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesByDriver(driver, pageable);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/scheduled-date/{date}")
    public ResponseEntity<Page<DeliveryResponseDTO>> getDeliveriesByScheduledDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        Page<DeliveryResponseDTO> deliveries = deliveryService.getDeliveriesByScheduledDate(date, pageable);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByTrackingNumber(@PathVariable String trackingNumber) {
        DeliveryResponseDTO delivery = deliveryService.getDeliveryByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(delivery);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> updateDelivery(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryDTO deliveryDTO) {
        DeliveryResponseDTO updatedDelivery = deliveryService.updateDelivery(id, deliveryDTO);
        return ResponseEntity.ok(updatedDelivery);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<DeliveryResponseDTO> startDelivery(@PathVariable Long id) {
        DeliveryResponseDTO updatedDelivery = deliveryService.startDelivery(id);
        return ResponseEntity.ok(updatedDelivery);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<DeliveryResponseDTO> completeDelivery(@PathVariable Long id) {
        DeliveryResponseDTO updatedDelivery = deliveryService.completeDelivery(id);
        return ResponseEntity.ok(updatedDelivery);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam DeliveryStatus status) {
        DeliveryResponseDTO updatedDelivery = deliveryService.updateDeliveryStatus(id, status);
        return ResponseEntity.ok(updatedDelivery);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}