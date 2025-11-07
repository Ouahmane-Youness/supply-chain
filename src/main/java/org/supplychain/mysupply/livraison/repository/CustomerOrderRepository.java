package org.supplychain.mysupply.livraison.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;
import org.supplychain.mysupply.livraison.model.CustomerOrder;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    boolean existsByOrderNumber(String orderNumber);

    Page<CustomerOrder> findByStatus(CustomerOrderStatus status, Pageable pageable);

    @Query("SELECT co FROM CustomerOrder co WHERE co.customer.idCustomer = :customerId")
    Page<CustomerOrder> findByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT co FROM CustomerOrder co WHERE co.orderNumber ILIKE %:searchTerm%")
    Page<CustomerOrder> findByOrderNumberContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT co FROM CustomerOrder co WHERE co.delivery IS NULL")
    Page<CustomerOrder> findOrdersWithoutDelivery(Pageable pageable);
}