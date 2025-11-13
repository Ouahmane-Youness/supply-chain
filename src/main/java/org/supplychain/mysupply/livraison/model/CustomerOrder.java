package org.supplychain.mysupply.livraison.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.supplychain.mysupply.common.model.BaseEntity;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"customer", "orderLines", "delivery"})
@ToString(exclude = {"customer", "orderLines", "delivery"})
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerOrderLine> orderLines = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerOrderStatus status = CustomerOrderStatus.EN_PREPARATION;

    @OneToOne(mappedBy = "customerOrder", cascade = CascadeType.ALL)
    private Delivery delivery;

    private String notes;
}