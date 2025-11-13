package org.supplychain.mysupply.approvisionnement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;
import org.supplychain.mysupply.approvisionnement.mapper.RawMaterialMapper;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.approvisionnement.service.interf.IRawMaterialService;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class RawMaterialService implements IRawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    @Override
    public RawMaterialResponseDTO createRawMaterial(RawMaterialDTO rawMaterialDTO) {
        if (rawMaterialRepository.existsByName(rawMaterialDTO.getName())) {
            throw new IllegalArgumentException("Material name already exists: " + rawMaterialDTO.getName());
        }

        RawMaterial rawMaterial = rawMaterialMapper.toEntity(rawMaterialDTO);
        RawMaterial savedMaterial = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toResponseDTO(savedMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialResponseDTO getRawMaterialById(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found with id: " + id));
        return rawMaterialMapper.toResponseDTO(rawMaterial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> getAllRawMaterials(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable)
                .map(rawMaterialMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> searchRawMaterials(String searchTerm, Pageable pageable) {
        return rawMaterialRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                .map(rawMaterialMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialResponseDTO> getLowStockMaterials(Pageable pageable) {
        return rawMaterialRepository.findLowStockMaterials(pageable)
                .map(rawMaterialMapper::toResponseDTO);
    }

    @Override
    public RawMaterialResponseDTO updateRawMaterial(Long id, RawMaterialDTO rawMaterialDTO) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found with id: " + id));

        if (!rawMaterial.getName().equals(rawMaterialDTO.getName()) &&
                rawMaterialRepository.existsByName(rawMaterialDTO.getName())) {
            throw new IllegalArgumentException("Material name already exists: " + rawMaterialDTO.getName());
        }

        rawMaterialMapper.updateEntityFromDTO(rawMaterialDTO, rawMaterial);
        RawMaterial updatedMaterial = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toResponseDTO(updatedMaterial);
    }

    @Override
    public RawMaterialResponseDTO updateStock(Long id, Integer newStock) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found with id: " + id));

        rawMaterial.setStock(newStock);
        rawMaterial.setLastRestockDate(LocalDate.now());
        RawMaterial updatedMaterial = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toResponseDTO(updatedMaterial);
    }

    @Override
    public void deleteRawMaterial(Long id) {
        if (!rawMaterialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Raw material not found with id: " + id);
        }

        long usageCount = rawMaterialRepository.countSupplyOrderLinesByMaterialId(id);
        if (usageCount > 0) {
            throw new UnauthorizedException("Cannot delete material that is used in supply orders");
        }

        rawMaterialRepository.deleteById(id);
    }
}