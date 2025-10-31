package org.supplychain.supplychain.repository.approvisionnement;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.RawMaterial;

import java.util.List;
import java.util.Optional;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Page<RawMaterial> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<RawMaterial> findByStockLessThan(Integer stockMin);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stock < rm.stockMin")
    List<RawMaterial> findCriticalStockMaterials();

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stock < rm.stockMin")
    Page<RawMaterial> findCriticalStockMaterialsWithPagination(Pageable pageable);

    Optional<RawMaterial> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT COUNT(bom) > 0 FROM BillOfMaterial bom WHERE bom.material.idMaterial = :materialId")
    boolean isUsedInBillOfMaterials(@Param("materialId") Long materialId);

    @Query("SELECT COUNT(sol) > 0 FROM SupplyOrderLine sol WHERE sol.rawMaterial.idMaterial = :materialId " +
            "AND sol.supplyOrder.status IN ('EN_ATTENTE', 'EN_COURS')")
    boolean hasActiveSupplyOrders(@Param("materialId") Long materialId);

    @Query("SELECT rm FROM RawMaterial rm LEFT JOIN FETCH rm.suppliers WHERE rm.idMaterial = :id")
    Optional<RawMaterial> findByIdWithSuppliers(@Param("id") Long id);

    @Query("SELECT DISTINCT rm FROM RawMaterial rm " +
            "LEFT JOIN FETCH rm.suppliers " +
            "WHERE rm.idMaterial IN :ids")
    List<RawMaterial> findAllByIdWithSuppliers(@Param("ids") List<Long> ids);

    @Query("SELECT rm FROM RawMaterial rm WHERE rm.stock - rm.reservedStock >= :requiredQuantity")
    List<RawMaterial> findMaterialsWithAvailableStock(@Param("requiredQuantity") Integer requiredQuantity);

    @Query("SELECT rm FROM RawMaterial rm " +
            "JOIN rm.suppliers s " +
            "WHERE s.idSupplier = :supplierId")
    List<RawMaterial> findMaterialsBySupplier(@Param("supplierId") Long supplierId);

    @Query(value = "SELECT rm.* FROM raw_materials rm " +
            "WHERE rm.stock < rm.stock_min " +
            "AND rm.id_material NOT IN (" +
            "   SELECT sol.raw_material_id FROM supply_order_lines sol " +
            "   JOIN supply_orders so ON sol.supply_order_id = so.id_order " +
            "   WHERE so.status = 'EN_ATTENTE' OR so.status = 'EN_COURS'" +
            ")",
            nativeQuery = true)
    List<RawMaterial> findCriticalMaterialsWithoutPendingOrders();
}

