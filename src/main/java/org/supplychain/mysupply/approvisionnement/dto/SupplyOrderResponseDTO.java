package org.supplychain.mysupply.approvisionnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrderResponseDTO {

    private Long idOrder;
    private String orderNumber;
    private SupplierResponseDTO supplier;
    private LocalDate orderDate;
    private SupplyOrderStatus status;
    private BigDecimal totalAmount;
    private List<SupplyOrderLineResponseDTO> orderLines;
}