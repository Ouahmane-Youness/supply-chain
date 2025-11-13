package org.supplychain.mysupply.approvisionnement.service.interf;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.approvisionnement.dto.SupplierDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplierResponseDTO;

public interface ISupplierService {

    SupplierResponseDTO createSupplier(SupplierDTO supplierDTO);

    SupplierResponseDTO getSupplierById(Long id);

    Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable);

    Page<SupplierResponseDTO> searchSuppliers(String searchTerm, Pageable pageable);

    SupplierResponseDTO updateSupplier(Long id, SupplierDTO supplierDTO);

    void deleteSupplier(Long id);
}