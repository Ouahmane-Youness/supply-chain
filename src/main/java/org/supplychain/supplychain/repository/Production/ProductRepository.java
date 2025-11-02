package org.supplychain.supplychain.repository.Production;

import org.supplychain.supplychain.enums.OrderStatus;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);


    boolean existsByNameAndIdProductNot(String name, Long idProduct);

    @Query("SELECT COUNT(po) FROM ProductionOrder po WHERE po.product.idProduct = :productId")
    Long countProductionOrdersByProductId(@Param("productId") Long productId);


    @Query("SELECT COUNT(po) FROM ProductionOrder po WHERE po.product.idProduct = :productId AND po.status = :status")
    Long countProductionOrdersByProductIdAndStatus(@Param("productId") Long productId, @Param("status") ProductionOrderStatus status);


    @Query("SELECT COUNT(op) FROM ProductOrder op JOIN op.order o WHERE op.product.idProduct = :productId AND o.status = :status")
    Long countOrdersByProductIdAndStatus(@Param("productId") Long productId, @Param("status") OrderStatus status);




}
