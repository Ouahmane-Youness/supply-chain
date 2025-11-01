package org.supplychain.supplychain.service.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialRequestDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialResponseDTO;
import org.supplychain.supplychain.dto.rawmaterial.RawMaterialSummaryDTO;

import java.util.List;

public interface RawMaterialService {

    RawMaterialResponseDTO createRawMaterial(RawMaterialRequestDTO requestDTO);

    RawMaterialResponseDTO updateRawMaterial(Long id, RawMaterialRequestDTO requestDTO);

    void deleteRawMaterial(Long id);

    RawMaterialResponseDTO getRawMaterialById(Long id);

    Page<RawMaterialSummaryDTO> getAllRawMaterials(Pageable pageable);

    Page<RawMaterialSummaryDTO> searchRawMaterials(String name, Pageable pageable);

    List<RawMaterialSummaryDTO> getCriticalStockMaterials();

    Page<RawMaterialSummaryDTO> getCriticalStockMaterialsWithPagination(Pageable pageable);

    boolean isMaterialUsed(Long id);
}