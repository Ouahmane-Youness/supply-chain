package org.supplychain.supplychain.dto.rawmaterial;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialRequestDTO {

    @NotBlank(message = "Material name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Size(max= 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Minimum stock threshold is required")
    @Min(value = 0, message = "Minimum stock cannot be negative")
    private Integer stockMin;

    @NotBlank(message = "Unit is required")
    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;

    private LocalDate lastRestockDate;

    private List<Long> supplierIds;
}
