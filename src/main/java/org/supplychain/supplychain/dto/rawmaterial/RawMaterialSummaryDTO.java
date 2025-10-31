package org.supplychain.supplychain.dto.rawmaterial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialSummaryDTO {

    private Long id;

    private String name;

    private Integer stock;

    private Integer availableStock;

    private Integer stockMin;

    private String unit;

    private Boolean isCriticalStock;
}