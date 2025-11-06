package org.supplychain.mysupply.approvisionnement.model;

import jakarta.persistence.*;
import lombok.*;
import org.supplychain.mysupply.common.model.BaseEntity;


@Entity
@Table(name = "suppliers")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"supplyOrders", "materials"})
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupplier;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private Double rating;

    private Integer leadTime;
}
