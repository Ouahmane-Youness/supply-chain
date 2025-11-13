package org.supplychain.mysupply.livraison.service.interf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.livraison.dto.OrderDTO;
import org.supplychain.mysupply.livraison.dto.OrderResponseDTO;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;

public interface ICustomerOrderService {

    OrderResponseDTO createOrder(OrderDTO orderDTO);

    OrderResponseDTO getOrderById(Long id);

    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    Page<OrderResponseDTO> getOrdersByStatus(CustomerOrderStatus status, Pageable pageable);

    Page<OrderResponseDTO> getOrdersByCustomer(Long customerId, Pageable pageable);

    Page<OrderResponseDTO> getOrdersWithoutDelivery(Pageable pageable);

    OrderResponseDTO updateOrderStatus(Long id, CustomerOrderStatus newStatus);

    void deleteOrder(Long id);
}