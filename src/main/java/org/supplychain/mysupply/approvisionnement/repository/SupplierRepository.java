package org.supplychain.mysupply.approvisionnement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.approvisionnement.model.Supplier;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE s.name ILIKE %:name% OR s.contact ILIKE %:name%")
    Page<Supplier> findByNameOrContactContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT COUNT(so) FROM SupplyOrder so WHERE so.supplier.idSupplier = :supplierId AND so.status IN ('EN_ATTENTE', 'EN_COURS')")
    long countActiveOrdersBySupplierId(@Param("supplierId") Long supplierId);
}