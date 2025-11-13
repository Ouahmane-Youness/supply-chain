package org.supplychain.mysupply.production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterialResponseDTO {

    private Long idBOM;
    private RawMaterialResponseDTO material;
    private Integer quantity;
    private boolean materialAvailable;
}