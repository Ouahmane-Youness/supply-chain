package org.supplychain.mysupply.approvisionnement.service.interf;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;

public interface IRawMaterialService {

    RawMaterialResponseDTO createRawMaterial(RawMaterialDTO rawMaterialDTO);

    RawMaterialResponseDTO getRawMaterialById(Long id);

    Page<RawMaterialResponseDTO> getAllRawMaterials(Pageable pageable);

    Page<RawMaterialResponseDTO> searchRawMaterials(String searchTerm, Pageable pageable);

    Page<RawMaterialResponseDTO> getLowStockMaterials(Pageable pageable);

    RawMaterialResponseDTO updateRawMaterial(Long id, RawMaterialDTO rawMaterialDTO);

    RawMaterialResponseDTO updateStock(Long id, Integer newStock);

    void deleteRawMaterial(Long id);
}