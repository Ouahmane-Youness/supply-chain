package org.supplychain.mysupply.production.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.production.model.BillOfMaterial;

import java.util.List;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {

    List<BillOfMaterial> findByProductIdProduct(Long productId);

    @Query("SELECT bom FROM BillOfMaterial bom WHERE bom.product.idProduct = :productId AND bom.material.idMaterial = :materialId")
    BillOfMaterial findByProductIdAndMaterialId(@Param("productId") Long productId, @Param("materialId") Long materialId);

    boolean existsByProductIdProductAndMaterialIdMaterial(Long productId, Long materialId);
}