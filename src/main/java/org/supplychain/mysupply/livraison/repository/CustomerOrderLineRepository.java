package org.supplychain.mysupply.livraison.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.livraison.model.CustomerOrderLine;

@Repository
public interface CustomerOrderLineRepository extends JpaRepository<CustomerOrderLine, Long> {
}