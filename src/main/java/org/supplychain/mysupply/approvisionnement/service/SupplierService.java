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

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierResponseDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + supplierDTO.getEmail());
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(savedSupplier);
    }

    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return supplierMapper.toResponseDTO(supplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> searchSuppliers(String searchTerm, Pageable pageable) {
        return supplierRepository.findByNameOrContactContaining(searchTerm, pageable)
                .map(supplierMapper::toResponseDTO);
    }

    public SupplierResponseDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        if (!supplier.getEmail().equals(supplierDTO.getEmail()) &&
                supplierRepository.existsByEmail(supplierDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + supplierDTO.getEmail());
        }

        supplierMapper.updateEntityFromDTO(supplierDTO, supplier);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }

        long activeOrdersCount = supplierRepository.countActiveOrdersBySupplierId(id);
        if (activeOrdersCount > 0) {
            throw new RuntimeException("Cannot delete supplier with active orders");
        }

        supplierRepository.deleteById(id);
    }
}