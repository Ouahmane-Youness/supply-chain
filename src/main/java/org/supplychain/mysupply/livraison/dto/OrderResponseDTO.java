package org.supplychain.mysupply.livraison.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long idOrder;
    private String orderNumber;
    private CustomerResponseDTO customer;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private CustomerOrderStatus status;
    private String notes;
    private List<OrderLineResponseDTO> orderLines;
    private DeliverySummaryDTO delivery;
}