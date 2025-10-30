package org.supplychain.supplychain.repository.modelDelivery;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{
    boolean existsByEmail(String email);
}
