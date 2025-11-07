package org.supplychain.mysupply.livraison.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {

    private Long idDelivery;
    private OrderResponseDTO order;
    private String deliveryAddress;
    private String city;
    private String driver;
    private String vehicle;
    private DeliveryStatus status;
    private LocalDate scheduledDate;
    private LocalDate actualDeliveryDate;
    private BigDecimal deliveryCost;
    private String trackingNumber;
    private String notes;
}