package org.supplychain.mysupply.livraison.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.livraison.model.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.name ILIKE %:searchTerm% OR c.email ILIKE %:searchTerm%")
    Page<Customer> findByNameOrEmailContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(co) FROM CustomerOrder co WHERE co.customer.idCustomer = :customerId AND co.status != 'LIVREE'")
    long countActiveOrdersByCustomerId(@Param("customerId") Long customerId);
}