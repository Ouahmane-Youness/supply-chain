package org.supplychain.mysupply.livraison.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    private Long idOrder;
    private String orderNumber;
    private CustomerResponseDTO customer;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private CustomerOrderStatus status;
    private String notes;
}