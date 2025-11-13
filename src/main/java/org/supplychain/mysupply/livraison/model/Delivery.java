package org.supplychain.mysupply.livraison.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.supplychain.mysupply.common.model.BaseEntity;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"customerOrder"})
@ToString(exclude = {"customerOrder"})
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDelivery;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_order_id", nullable = false, unique = true)
    private CustomerOrder customerOrder;

    @Column(nullable = false)
    private String deliveryAddress;

    private String city;

    private String driver;

    private String vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PLANIFIEE;

    private LocalDate scheduledDate;

    private LocalDate actualDeliveryDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryCost;

    private String trackingNumber;

    private String notes;
}