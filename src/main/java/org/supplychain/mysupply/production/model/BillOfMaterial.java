package org.supplychain.mysupply.production.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.common.model.BaseEntity;

@Entity
@Table(name = "bill_of_materials",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "material_id"}))
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "material"})
@ToString(exclude = {"product", "material"})
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBOM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private RawMaterial material;

    @Column(nullable = false)
    private Integer quantity;
}