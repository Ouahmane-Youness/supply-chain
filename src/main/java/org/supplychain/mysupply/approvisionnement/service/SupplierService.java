package org.supplychain.mysupply.approvisionnement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.approvisionnement.dto.SupplierDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplierResponseDTO;
import org.supplychain.mysupply.approvisionnement.mapper.SupplierMapper;
import org.supplychain.mysupply.approvisionnement.model.Supplier;
import org.supplychain.mysupply.approvisionnement.repository.SupplierRepository;
import org.supplychain.mysupply.approvisionnement.service.interf.ISupplierService;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponseDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + supplierDTO.getEmail());
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return supplierMapper.toResponseDTO(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> searchSuppliers(String searchTerm, Pageable pageable) {
        return supplierRepository.findByNameOrContactContaining(searchTerm, pageable)
                .map(supplierMapper::toResponseDTO);
    }

    @Override
    public SupplierResponseDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        if (!supplier.getEmail().equals(supplierDTO.getEmail()) &&
                supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + supplierDTO.getEmail());
        }

        supplierMapper.updateEntityFromDTO(supplierDTO, supplier);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(updatedSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }

        long activeOrdersCount = supplierRepository.countActiveOrdersBySupplierId(id);
        if (activeOrdersCount > 0) {
            throw new UnauthorizedException("Cannot delete supplier with " + activeOrdersCount + " active order(s). Only suppliers with no active orders (EN_ATTENTE or EN_COURS) can be deleted.");
        }

        supplierRepository.deleteById(id);
    }
}