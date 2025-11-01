package org.supplychain.supplychain.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private Integer productionTime;
    private BigDecimal cost;
    private Integer stock;
    private Integer minimumStock;
    private String unit;
}
