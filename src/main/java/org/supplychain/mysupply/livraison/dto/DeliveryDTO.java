package org.supplychain.mysupply.livraison.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {

    private Long idDelivery;

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private String city;

    private String driver;

    private String vehicle;

    private LocalDate scheduledDate;

    private BigDecimal deliveryCost;

    private String notes;
}