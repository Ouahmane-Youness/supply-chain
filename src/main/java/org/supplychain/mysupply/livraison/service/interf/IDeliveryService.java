package org.supplychain.mysupply.livraison.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.livraison.dto.DeliveryDTO;
import org.supplychain.mysupply.livraison.dto.DeliveryResponseDTO;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;

import java.time.LocalDate;

public interface IDeliveryService {

    DeliveryResponseDTO createDelivery(DeliveryDTO deliveryDTO);

    DeliveryResponseDTO getDeliveryById(Long id);

    Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable);

    Page<DeliveryResponseDTO> getDeliveriesByStatus(DeliveryStatus status, Pageable pageable);

    Page<DeliveryResponseDTO> getDeliveriesByDriver(String driver, Pageable pageable);

    Page<DeliveryResponseDTO> getDeliveriesByScheduledDate(LocalDate date, Pageable pageable);

    DeliveryResponseDTO getDeliveryByTrackingNumber(String trackingNumber);

    DeliveryResponseDTO updateDelivery(Long id, DeliveryDTO deliveryDTO);

    DeliveryResponseDTO startDelivery(Long id);

    DeliveryResponseDTO completeDelivery(Long id);

    DeliveryResponseDTO updateDeliveryStatus(Long id, DeliveryStatus newStatus);

    void deleteDelivery(Long id);
}