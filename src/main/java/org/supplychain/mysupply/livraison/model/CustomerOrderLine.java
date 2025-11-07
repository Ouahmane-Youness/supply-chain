package org.supplychain.mysupply.livraison.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.supplychain.mysupply.common.model.BaseEntity;
import org.supplychain.mysupply.production.model.Product;

import java.math.BigDecimal;

@Entity
@Table(name = "customer_order_lines")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"customerOrder", "product"})
@ToString(exclude = {"customerOrder", "product"})
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrderLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_order_id", nullable = false)
    private CustomerOrder customerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
}