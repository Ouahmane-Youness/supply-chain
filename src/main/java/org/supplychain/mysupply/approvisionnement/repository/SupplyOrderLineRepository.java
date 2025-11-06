package org.supplychain.mysupply.approvisionnement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrderLine;

@Repository
public interface SupplyOrderLineRepository extends JpaRepository<SupplyOrderLine, Long> {
}