package org.supplychain.supplychain.repository.modelSupplier;

import org.supplychain.supplychain.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByNameContainingIgnoreCase(String name);
    boolean existsByEmail(String email);
}
