package org.supplychain.mysupply.production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long idProduct;
    private String name;
    private String description;
    private Integer productionTime;
    private BigDecimal cost;
    private Integer stock;
    private Integer minimumStock;
    private String unit;
    private boolean lowStock;
    private List<BillOfMaterialResponseDTO> billOfMaterials;
}