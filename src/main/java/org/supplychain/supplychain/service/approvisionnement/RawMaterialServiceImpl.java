package org.supplychain.supplychain.service.approvisionnement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialRequestDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialResponseDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialSummaryDTO;
import org.supplychain.supplychain.mapper.RawMaterialMapper;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialMapper mapper;

    @Override
    @Transactional
    public RawMaterialResponseDTO createRawMaterial(RawMaterialRequestDTO requestDTO) {
        log.info("Creating raw material: {}", requestDTO.getName());

        if (rawMaterialRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Raw material with name '" + requestDTO.getName() + "' already exists");
        }

        RawMaterial rawMaterial = mapper.toEntity(requestDTO);

        if (requestDTO.getSupplierIds() != null && !requestDTO.getSupplierIds().isEmpty()) {
            List<Supplier> suppliers = supplierRepository.findAllById(requestDTO.getSupplierIds());
            if (suppliers.size() != requestDTO.getSupplierIds().size()) {
                throw new IllegalArgumentException("One or more supplier IDs are invalid");
            }
            rawMaterial.setSuppliers(suppliers);
        }

        if (requestDTO.getLastRestockDate() != null) {
            rawMaterial.setLastRestockDate(requestDTO.getLastRestockDate());
        }

        RawMaterial saved = rawMaterialRepository.save(rawMaterial);
        log.info("Raw material created with ID: {}", saved.getIdMaterial());

        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public RawMaterialResponseDTO updateRawMaterial(Long id, RawMaterialRequestDTO requestDTO) {
        log.info("Updating raw material with ID: {}", id);

        RawMaterial existing = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Raw material not found with ID: " + id));

        if (!existing.getName().equals(requestDTO.getName()) &&
                rawMaterialRepository.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("Raw material with name '" + requestDTO.getName() + "' already exists");
        }

        mapper.updateEntityFromDTO(requestDTO, existing);

        if (requestDTO.getSupplierIds() != null) {
            if (requestDTO.getSupplierIds().isEmpty()) {
                existing.getSuppliers().clear();
            } else {
                List<Supplier> suppliers = supplierRepository.findAllById(requestDTO.getSupplierIds());
                if (suppliers.size() != requestDTO.getSupplierIds().size()) {
                    throw new IllegalArgumentException("One or more supplier IDs are invalid");
                }
                existing.getSuppliers().clear();
                existing.getSuppliers().addAll(suppliers);
            }
        }

        if (requestDTO.getLastRestockDate() != null) {
            existing.setLastRestockDate(requestDTO.getLastRestockDate());
        }

        RawMaterial updated = rawMaterialRepository.save(existing);
        log.info("Raw material updated: {}", updated.getIdMaterial());

        return mapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteRawMaterial(Long id) {
        log.info("Deleting raw material with ID: {}", id);

        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Raw material not found with ID: " + id));

        if (rawMaterialRepository.isUsedInBillOfMaterials(id)) {
            throw new IllegalStateException("Cannot delete raw material. It is used in one or more Bill of Materials");
        }

        if (rawMaterialRepository.hasActiveSupplyOrders(id)) {
            throw new IllegalStateException("Cannot delete raw material. It has active supply orders");
        }

        rawMaterialRepository.delete(rawMaterial);
        log.info("Raw material deleted: {}", id);
    }

    @Override
    public RawMaterialResponseDTO getRawMaterialById(Long id) {
        log.debug("Fetching raw material with ID: {}", id);

        RawMaterial rawMaterial = rawMaterialRepository.findByIdWithSuppliers(id)
                .orElseThrow(() -> new IllegalArgumentException("Raw material not found with ID: " + id));

        return mapper.toResponseDTO(rawMaterial);
    }

    @Override
    public Page<RawMaterialSummaryDTO> getAllRawMaterials(Pageable pageable) {
        log.debug("Fetching all raw materials with pagination");

        Page<RawMaterial> materialsPage = rawMaterialRepository.findAll(pageable);
        return materialsPage.map(mapper::toSummaryDTO);
    }

    @Override
    public Page<RawMaterialSummaryDTO> searchRawMaterials(String name, Pageable pageable) {
        log.debug("Searching raw materials by name: {}", name);

        Page<RawMaterial> materialsPage = rawMaterialRepository.findByNameContainingIgnoreCase(name, pageable);
        return materialsPage.map(mapper::toSummaryDTO);
    }

    @Override
    public List<RawMaterialSummaryDTO> getCriticalStockMaterials() {
        log.debug("Fetching critical stock materials");

        List<RawMaterial> criticalMaterials = rawMaterialRepository.findCriticalStockMaterials();
        return mapper.toSummaryDTOList(criticalMaterials);
    }

    @Override
    public Page<RawMaterialSummaryDTO> getCriticalStockMaterialsWithPagination(Pageable pageable) {
        log.debug("Fetching critical stock materials with pagination");

        Page<RawMaterial> materialsPage = rawMaterialRepository.findCriticalStockMaterialsWithPagination(pageable);
        return materialsPage.map(mapper::toSummaryDTO);
    }

    @Override
    public boolean isMaterialUsed(Long id) {
        log.debug("Checking if material is used: {}", id);

        return rawMaterialRepository.isUsedInBillOfMaterials(id) ||
                rawMaterialRepository.hasActiveSupplyOrders(id);
    }
}