package org.supplychain.supplychain.dto.rawmaterial;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Integer stock;

    private Integer reservedStock;

    private Integer availableStock;

    private Integer stockMin;

    private String unit;

    private Boolean isCriticalStock;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastRestockDate;

    private List<SupplierSummaryDTO> suppliers;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    private String updatedBy;
}