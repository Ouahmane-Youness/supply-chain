package org.supplychain.mysupply.approvisionnement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialDTO {

    private Long idMaterial;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be positive or zero")
    private Integer stock;

    @NotNull(message = "Reserved stock is required")
    @PositiveOrZero(message = "Reserved stock must be positive or zero")
    private Integer reservedStock;

    @NotNull(message = "Minimum stock is required")
    @PositiveOrZero(message = "Minimum stock must be positive or zero")
    private Integer stockMin;

    @NotBlank(message = "Unit is required")
    private String unit;

    private List<Long> supplierIds;
}