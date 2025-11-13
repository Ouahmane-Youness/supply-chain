package org.supplychain.mysupply.approvisionnement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;

import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Page<RawMaterial> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stock <= rm.stockMin")
    Page<RawMaterial> findLowStockMaterials(Pageable pageable);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stock <= rm.stockMin")
    List<RawMaterial> findAllLowStockMaterials();

    @Query("SELECT COUNT(sol) FROM SupplyOrderLine sol WHERE sol.rawMaterial.idMaterial = :materialId")
    long countSupplyOrderLinesByMaterialId(@Param("materialId") Long materialId);

    boolean existsByName(String name);
}