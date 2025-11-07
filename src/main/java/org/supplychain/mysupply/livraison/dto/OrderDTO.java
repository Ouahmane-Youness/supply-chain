package org.supplychain.mysupply.livraison.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long idOrder;

    @NotBlank(message = "Order number is required")
    private String OrderNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Order date is required")
    private String OrderDate;

    private String notes;

    @Valid
    private List<OrderLineDTO> orderLines;


}
