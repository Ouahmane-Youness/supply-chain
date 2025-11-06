package org.supplychain.mysupply.production.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.supplychain.mysupply.common.model.BaseEntity;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;

import java.time.LocalDate;

@Entity
@Table(name = "production_orders")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product"})
@ToString(exclude = {"product"})
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductionOrderStatus status = ProductionOrderStatus.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.STANDARD;

    @Column(nullable = false)
    private LocalDate orderDate;

    private LocalDate startDate;

    private LocalDate estimatedEndDate;

    private LocalDate actualEndDate;

    @Column(nullable = false)
    private Integer estimatedProductionTimeHours;
}