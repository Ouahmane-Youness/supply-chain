package org.supplychain.mysupply.production.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long idProduct;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Production time is required")
    @Positive(message = "Production time must be positive")
    private Integer productionTime;

    @NotNull(message = "Cost is required")
    @Positive(message = "Cost must be positive")
    private BigDecimal cost;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be positive or zero")
    private Integer stock;

    @NotNull(message = "Minimum stock is required")
    @PositiveOrZero(message = "Minimum stock must be positive or zero")
    private Integer minimumStock;

    @NotBlank(message = "Unit is required")
    private String unit;

    @Valid
    private List<BillOfMaterialDTO> billOfMaterials;
}