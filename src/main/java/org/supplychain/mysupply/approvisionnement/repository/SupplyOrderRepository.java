package org.supplychain.mysupply.approvisionnement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrder;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    Page<SupplyOrder> findByStatus(SupplyOrderStatus status, Pageable pageable);

    @Query("SELECT so FROM SupplyOrder so WHERE so.supplier.idSupplier = :supplierId")
    Page<SupplyOrder> findBySupplier(@Param("supplierId") Long supplierId, Pageable pageable);

    @Query("SELECT so FROM SupplyOrder so WHERE so.orderNumber ILIKE %:searchTerm%")
    Page<SupplyOrder> findByOrderNumberContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
}