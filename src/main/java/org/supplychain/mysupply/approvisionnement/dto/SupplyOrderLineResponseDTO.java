package org.supplychain.mysupply.approvisionnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrderLineResponseDTO {

    private Long idLine;
    private RawMaterialResponseDTO rawMaterial;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}