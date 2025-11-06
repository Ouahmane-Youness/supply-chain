package org.supplychain.mysupply.approvisionnement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;
import org.supplychain.mysupply.approvisionnement.service.RawMaterialService;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @PostMapping
    public ResponseEntity<RawMaterialResponseDTO> createRawMaterial(@Valid @RequestBody RawMaterialDTO rawMaterialDTO) {
        RawMaterialResponseDTO createdMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMaterial);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> getRawMaterialById(@PathVariable Long id) {
        RawMaterialResponseDTO material = rawMaterialService.getRawMaterialById(id);
        return ResponseEntity.ok(material);
    }

    @GetMapping
    public ResponseEntity<Page<RawMaterialResponseDTO>> getAllRawMaterials(Pageable pageable) {
        Page<RawMaterialResponseDTO> materials = rawMaterialService.getAllRawMaterials(pageable);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RawMaterialResponseDTO>> searchRawMaterials(
            @RequestParam String searchTerm,
            Pageable pageable) {
        Page<RawMaterialResponseDTO> materials = rawMaterialService.searchRawMaterials(searchTerm, pageable);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<Page<RawMaterialResponseDTO>> getLowStockMaterials(Pageable pageable) {
        Page<RawMaterialResponseDTO> materials = rawMaterialService.getLowStockMaterials(pageable);
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> updateRawMaterial(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialDTO rawMaterialDTO) {
        RawMaterialResponseDTO updatedMaterial = rawMaterialService.updateRawMaterial(id, rawMaterialDTO);
        return ResponseEntity.ok(updatedMaterial);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<RawMaterialResponseDTO> updateStock(
            @PathVariable Long id,
            @RequestParam Integer newStock) {
        RawMaterialResponseDTO updatedMaterial = rawMaterialService.updateStock(id, newStock);
        return ResponseEntity.ok(updatedMaterial);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.noContent().build();
    }
}