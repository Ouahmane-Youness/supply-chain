package org.supplychain.mysupply.approvisionnement.model;

import jakarta.persistence.*;
import lombok.*;
import org.supplychain.mysupply.common.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    List<SupplyOrder> supplyOrders = new ArrayList<>();

    @ManyToMany(mappedBy  = "suppliers")
    private List<RawMaterial> materials = new ArrayList<>();
}
