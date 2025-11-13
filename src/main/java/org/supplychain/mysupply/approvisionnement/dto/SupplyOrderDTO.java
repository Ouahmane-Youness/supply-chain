package org.supplychain.mysupply.approvisionnement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrderDTO {

    private Long idOrder;

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Order date is required")
    private LocalDate orderDate;

    private SupplyOrderStatus status;

    @Valid
    private List<SupplyOrderLineDTO> orderLines;
}