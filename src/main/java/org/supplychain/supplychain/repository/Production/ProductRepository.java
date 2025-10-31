package org.supplychain.supplychain.repository.Production;

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
}
