package org.supplychain.mysupply.livraison.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.livraison.dto.DeliveryDTO;
import org.supplychain.mysupply.livraison.dto.DeliveryResponseDTO;
import org.supplychain.mysupply.livraison.dto.OrderResponseDTO;
import org.supplychain.mysupply.livraison.enums.DeliveryStatus;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;
import org.supplychain.mysupply.livraison.mapper.DeliveryMapper;
import org.supplychain.mysupply.livraison.mapper.CustomerOrderMapper;
import org.supplychain.mysupply.livraison.model.Delivery;
import org.supplychain.mysupply.livraison.model.CustomerOrder;
import org.supplychain.mysupply.livraison.repository.DeliveryRepository;
import org.supplychain.mysupply.livraison.repository.CustomerOrderRepository;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final DeliveryMapper deliveryMapper;
    private final CustomerOrderMapper customerOrderMapper;

    public DeliveryResponseDTO createDelivery(DeliveryDTO deliveryDTO) {
        CustomerOrder customerOrder = customerOrderRepository.findById(deliveryDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + deliveryDTO.getOrderId()));

        if (customerOrder.getDelivery() != null) {
            throw new RuntimeException("Order already has a delivery assigned");
        }

        if (customerOrder.getStatus() != CustomerOrderStatus.EN_PREPARATION) {
            throw new RuntimeException("Can only create delivery for orders in EN_PREPARATION status");
        }

        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);
        delivery.setCustomerOrder(customerOrder);
        delivery.setStatus(DeliveryStatus.PLANIFIEE);
        delivery.setTrackingNumber(generateTrackingNumber());

        if (delivery.getDeliveryAddress() == null || delivery.getDeliveryAddress().isEmpty()) {
            delivery.setDeliveryAddress(customerOrder.getCustomer().getAddress());
            delivery.setCity(customerOrder.getCustomer().getCity());
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);
        return mapToResponseDTO(savedDelivery);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
        return mapToResponseDTO(delivery);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getDeliveriesByStatus(DeliveryStatus status, Pageable pageable) {
        return deliveryRepository.findByStatus(status, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getDeliveriesByDriver(String driver, Pageable pageable) {
        return deliveryRepository.findByDriver(driver, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getDeliveriesByScheduledDate(LocalDate date, Pageable pageable) {
        return deliveryRepository.findByScheduledDate(date, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryByTrackingNumber(String trackingNumber) {
        Delivery delivery = deliveryRepository.findByTrackingNumber(trackingNumber);
        if (delivery == null) {
            throw new RuntimeException("Delivery not found with tracking number: " + trackingNumber);
        }
        return mapToResponseDTO(delivery);
    }

    public DeliveryResponseDTO updateDelivery(Long id, DeliveryDTO deliveryDTO) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        if (delivery.getStatus() == DeliveryStatus.LIVREE) {
            throw new RuntimeException("Cannot modify completed delivery");
        }

        deliveryMapper.updateEntityFromDTO(deliveryDTO, delivery);
        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToResponseDTO(updatedDelivery);
    }

    public DeliveryResponseDTO startDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        if (delivery.getStatus() != DeliveryStatus.PLANIFIEE) {
            throw new RuntimeException("Can only start deliveries in PLANIFIEE status");
        }

        delivery.setStatus(DeliveryStatus.EN_COURS);
        delivery.getCustomerOrder().setStatus(CustomerOrderStatus.EN_ROUTE);
        customerOrderRepository.save(delivery.getCustomerOrder());

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToResponseDTO(updatedDelivery);
    }

    public DeliveryResponseDTO completeDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        if (delivery.getStatus() != DeliveryStatus.EN_COURS) {
            throw new RuntimeException("Can only complete deliveries that are in progress");
        }

        delivery.setStatus(DeliveryStatus.LIVREE);
        delivery.setActualDeliveryDate(LocalDate.now());
        delivery.getCustomerOrder().setStatus(CustomerOrderStatus.LIVREE);
        customerOrderRepository.save(delivery.getCustomerOrder());

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return mapToResponseDTO(updatedDelivery);
    }

    public DeliveryResponseDTO updateDeliveryStatus(Long id, DeliveryStatus newStatus) {
        if (newStatus == DeliveryStatus.EN_COURS) {
            return startDelivery(id);
        } else if (newStatus == DeliveryStatus.LIVREE) {
            return completeDelivery(id);
        } else {
            Delivery delivery = deliveryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
            delivery.setStatus(newStatus);
            Delivery updatedDelivery = deliveryRepository.save(delivery);
            return mapToResponseDTO(updatedDelivery);
        }
    }

    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        if (delivery.getStatus() != DeliveryStatus.PLANIFIEE) {
            throw new RuntimeException("Can only delete deliveries that haven't started yet");
        }

        deliveryRepository.deleteById(id);
    }

    private DeliveryResponseDTO mapToResponseDTO(Delivery delivery) {
        DeliveryResponseDTO responseDTO = deliveryMapper.toResponseDTO(delivery);

        if (delivery.getCustomerOrder() != null) {
            responseDTO.setOrder(deliveryMapper.toOrderSummaryDTO(delivery.getCustomerOrder()));

        }

        return responseDTO;
    }
}