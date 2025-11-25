package org.supplychain.mysupply.approvisionnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialResponseDTO {

    private Long idMaterial;
    private String name;
    private String description;
    private Integer stock;
    private Integer reservedStock;
    private Integer stockMin;
    private String unit;
    private LocalDate lastRestockDate;
    private boolean lowStock;
    private List<SupplierResponseDTO> suppliers;
}