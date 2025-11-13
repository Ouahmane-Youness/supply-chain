package org.supplychain.mysupply.production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderResponseDTO {

    private Long idOrder;
    private String orderNumber;
    private ProductResponseDTO product;
    private Integer quantity;
    private ProductionOrderStatus status;
    private Priority priority;
    private LocalDate orderDate;
    private LocalDate startDate;
    private LocalDate estimatedEndDate;
    private LocalDate actualEndDate;
    private Integer estimatedProductionTimeHours;
}