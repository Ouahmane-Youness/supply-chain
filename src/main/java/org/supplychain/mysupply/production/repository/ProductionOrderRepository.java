package org.supplychain.mysupply.production.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.production.enums.Priority;
import org.supplychain.mysupply.production.enums.ProductionOrderStatus;
import org.supplychain.mysupply.production.model.ProductionOrder;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    Page<ProductionOrder> findByStatus(ProductionOrderStatus status, Pageable pageable);

    Page<ProductionOrder> findByPriority(Priority priority, Pageable pageable);

    @Query("SELECT po FROM ProductionOrder po WHERE po.product.idProduct = :productId")
    Page<ProductionOrder> findByProduct(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT po FROM ProductionOrder po ORDER BY " +
            "CASE po.priority " +
            "WHEN 'URGENT' THEN 1 " +
            "WHEN 'HIGH' THEN 2 " +
            "WHEN 'STANDARD' THEN 3 " +
            "WHEN 'LOW' THEN 4 " +
            "END, po.orderDate")
    Page<ProductionOrder> findAllOrderedByPriorityAndDate(Pageable pageable);
}