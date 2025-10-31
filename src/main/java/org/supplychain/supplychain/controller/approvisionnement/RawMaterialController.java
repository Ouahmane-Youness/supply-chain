package org.supplychain.supplychain.controller.approvisionnement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.rawmaterial.PageResponseDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialRequestDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialResponseDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialSummaryDTO;
import org.supplychain.supplychain.service.approvisionnement.RawMaterialService;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Slf4j
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @PostMapping
    public ResponseEntity<RawMaterialResponseDTO> createRawMaterial(
            @Valid @RequestBody RawMaterialRequestDTO requestDTO) {
        log.info("REST request to create raw material: {}", requestDTO.getName());
        RawMaterialResponseDTO response = rawMaterialService.createRawMaterial(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> updateRawMaterial(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialRequestDTO requestDTO) {
        log.info("REST request to update raw material with ID: {}", id);
        RawMaterialResponseDTO response = rawMaterialService.updateRawMaterial(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        log.info("REST request to delete raw material with ID: {}", id);
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialResponseDTO> getRawMaterialById(@PathVariable Long id) {
        log.info("REST request to get raw material with ID: {}", id);
        RawMaterialResponseDTO response = rawMaterialService.getRawMaterialById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<RawMaterialSummaryDTO>> getAllRawMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idMaterial") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        log.info("REST request to get all raw materials - page: {}, size: {}", page, size);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<RawMaterialSummaryDTO> materialsPage = rawMaterialService.getAllRawMaterials(pageable);

        PageResponseDTO<RawMaterialSummaryDTO> response = new PageResponseDTO<>(
                materialsPage.getContent(),
                materialsPage.getNumber(),
                materialsPage.getSize(),
                materialsPage.getTotalElements(),
                materialsPage.getTotalPages(),
                materialsPage.isFirst(),
                materialsPage.isLast(),
                materialsPage.hasNext(),
                materialsPage.hasPrevious()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<RawMaterialSummaryDTO>> searchRawMaterials(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to search raw materials by name: {}", name);

        Pageable pageable = PageRequest.of(page, size);
        Page<RawMaterialSummaryDTO> materialsPage = rawMaterialService.searchRawMaterials(name, pageable);

        PageResponseDTO<RawMaterialSummaryDTO> response = new PageResponseDTO<>(
                materialsPage.getContent(),
                materialsPage.getNumber(),
                materialsPage.getSize(),
                materialsPage.getTotalElements(),
                materialsPage.getTotalPages(),
                materialsPage.isFirst(),
                materialsPage.isLast(),
                materialsPage.hasNext(),
                materialsPage.hasPrevious()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/critical-stock")
    public ResponseEntity<List<RawMaterialSummaryDTO>> getCriticalStockMaterials() {
        log.info("REST request to get critical stock materials");
        List<RawMaterialSummaryDTO> criticalMaterials = rawMaterialService.getCriticalStockMaterials();
        return ResponseEntity.ok(criticalMaterials);
    }

    @GetMapping("/critical-stock/paginated")
    public ResponseEntity<PageResponseDTO<RawMaterialSummaryDTO>> getCriticalStockMaterialsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to get critical stock materials with pagination");

        Pageable pageable = PageRequest.of(page, size);
        Page<RawMaterialSummaryDTO> materialsPage = rawMaterialService.getCriticalStockMaterialsWithPagination(pageable);

        PageResponseDTO<RawMaterialSummaryDTO> response = new PageResponseDTO<>(
                materialsPage.getContent(),
                materialsPage.getNumber(),
                materialsPage.getSize(),
                materialsPage.getTotalElements(),
                materialsPage.getTotalPages(),
                materialsPage.isFirst(),
                materialsPage.isLast(),
                materialsPage.hasNext(),
                materialsPage.hasPrevious()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/is-used")
    public ResponseEntity<Boolean> isMaterialUsed(@PathVariable Long id) {
        log.info("REST request to check if material is used: {}", id);
        boolean isUsed = rawMaterialService.isMaterialUsed(id);
        return ResponseEntity.ok(isUsed);
    }
}