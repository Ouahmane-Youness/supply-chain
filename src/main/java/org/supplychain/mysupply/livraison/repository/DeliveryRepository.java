package org.supplychain.mysupply.livraison.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;
import org.supplychain.mysupply.livraison.model.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);

    Page<Delivery> findByDriver(String driver, Pageable pageable);

    @Query("SELECT d FROM Delivery d WHERE d.scheduledDate = :date")
    Page<Delivery> findByScheduledDate(@Param("date") java.time.LocalDate date, Pageable pageable);

    @Query("SELECT d FROM Delivery d WHERE d.trackingNumber = :trackingNumber")
    Delivery findByTrackingNumber(@Param("trackingNumber") String trackingNumber);
}