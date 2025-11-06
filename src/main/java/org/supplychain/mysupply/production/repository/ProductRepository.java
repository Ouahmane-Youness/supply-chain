package org.supplychain.mysupply.production.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.production.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stock <= p.minimumStock")
    Page<Product> findLowStockProducts(Pageable pageable);

    @Query("SELECT COUNT(po) FROM ProductionOrder po WHERE po.product.idProduct = :productId")
    long countProductionOrdersByProductId(@Param("productId") Long productId);
}