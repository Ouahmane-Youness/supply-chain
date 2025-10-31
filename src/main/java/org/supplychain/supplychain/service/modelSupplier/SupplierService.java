package org.supplychain.supplychain.service.modelSupplier;

import org.supplychain.supplychain.dto.supplier.SupplierDTO;
import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierDTO dto);
    SupplierDTO updateSupplier(Long id, SupplierDTO dto);
    void deleteSupplier(Long id);
    List<SupplierDTO> getAllSuppliers();
    List<SupplierDTO> searchSupplierByName(String name);
}
