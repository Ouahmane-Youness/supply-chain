package org.supplychain.supplychain.service.modelSupplier.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.supplier.SupplierDTO;
import org.supplychain.supplychain.mapper.modelSupplier.SupplierMapper;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.service.modelSupplier.SupplierService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierDTO createSupplier(SupplierDTO dto) {
        if (supplierRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        Supplier supplier = supplierMapper.toEntity(dto);
        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO dto) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));
        existing.setName(dto.getName());
        existing.setContact(dto.getContact());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setRating(dto.getRating());
        existing.setLeadTime(dto.getLeadTime());
        return supplierMapper.toDTO(supplierRepository.save(existing));
    }

//    @Override
//    public void deleteSupplier(Long id) {
//        Supplier supplier = supplierRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));
//        if (!supplier.getSupplyOrders().isEmpty()) {
//            throw new RuntimeException("Impossible de supprimer : des commandes actives existent");
//        }
//        supplierRepository.delete(supplier);
//    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));

        // check exit supplirOrder before delte
        if (!supplier.getSupplyOrders().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : des commandes actives existent");
        }

        // check supplier -> material before deleting
        if (!supplier.getMaterials().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : des matières premières sont associées");
        }

        // no relation delte supplier
        supplierRepository.delete(supplier);
    }



    @Override
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierDTO> searchSupplierByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }
}
