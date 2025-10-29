package org.supplychain.supplychain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.Priority;
import org.supplychain.supplychain.enums.ProductionOrderStatus;

import java.time.LocalDate;

@Entity
@Table(name = "production_orders")
@Data
@EqualsAndHashCode(callSuper = true)
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

    private LocalDate plannedStartDate;

    private LocalDate actualStartDate;

    private LocalDate plannedEndDate;

    private LocalDate actualEndDate;

    @Column(nullable = false)
    private Boolean materialsReserved = false;
}