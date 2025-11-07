package org.supplychain.mysupply.livraison.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.livraison.dto.OrderDTO;
import org.supplychain.mysupply.livraison.dto.OrderLineDTO;
import org.supplychain.mysupply.livraison.dto.OrderResponseDTO;
import org.supplychain.mysupply.livraison.enums.CustomerOrderStatus;
import org.supplychain.mysupply.livraison.mapper.CustomerOrderMapper;
import org.supplychain.mysupply.livraison.mapper.CustomerOrderLineMapper;
import org.supplychain.mysupply.livraison.mapper.DeliveryMapper;
import org.supplychain.mysupply.livraison.model.Customer;
import org.supplychain.mysupply.livraison.model.CustomerOrder;
import org.supplychain.mysupply.livraison.model.CustomerOrderLine;
import org.supplychain.mysupply.livraison.repository.CustomerRepository;
import org.supplychain.mysupply.livraison.repository.CustomerOrderRepository;
import org.supplychain.mysupply.production.model.Product;
import org.supplychain.mysupply.production.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderMapper customerOrderMapper;
    private final CustomerOrderLineMapper customerOrderLineMapper;
    private final DeliveryMapper deliveryMapper;

    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        if (customerOrderRepository.existsByOrderNumber(orderDTO.getOrderNumber())) {
            throw new RuntimeException("Order number already exists: " + orderDTO.getOrderNumber());
        }

        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + orderDTO.getCustomerId()));

        validateProductAvailability(orderDTO.getOrderLines());

        CustomerOrder customerOrder = customerOrderMapper.toEntity(orderDTO);
        customerOrder.setCustomer(customer);
        customerOrder.setStatus(CustomerOrderStatus.EN_PREPARATION);

        List<CustomerOrderLine> orderLines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderLineDTO lineDTO : orderDTO.getOrderLines()) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + lineDTO.getProductId()));

            CustomerOrderLine orderLine = customerOrderLineMapper.toEntity(lineDTO);
            orderLine.setCustomerOrder(customerOrder);
            orderLine.setProduct(product);

            BigDecimal lineTotalPrice = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            orderLine.setTotalPrice(lineTotalPrice);
            totalAmount = totalAmount.add(lineTotalPrice);

            orderLines.add(orderLine);
        }

        customerOrder.setOrderLines(orderLines);
        customerOrder.setTotalAmount(totalAmount);

        consumeProductStock(orderLines);

        CustomerOrder savedOrder = customerOrderRepository.save(customerOrder);
        return mapToResponseDTO(savedOrder);
    }

    private void validateProductAvailability(List<OrderLineDTO> orderLines) {
        List<String> insufficientProducts = new ArrayList<>();

        for (OrderLineDTO lineDTO : orderLines) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + lineDTO.getProductId()));

            if (product.getStock() < lineDTO.getQuantity()) {
                insufficientProducts.add(String.format(
                        "%s (need %d, available %d)",
                        product.getName(),
                        lineDTO.getQuantity(),
                        product.getStock()
                ));
            }
        }

        if (!insufficientProducts.isEmpty()) {
            throw new RuntimeException("Insufficient product stock: " + String.join(", ", insufficientProducts));
        }
    }

    private void consumeProductStock(List<CustomerOrderLine> orderLines) {
        for (CustomerOrderLine orderLine : orderLines) {
            Product product = orderLine.getProduct();
            product.setStock(product.getStock() - orderLine.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToResponseDTO(customerOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return customerOrderRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByStatus(CustomerOrderStatus status, Pageable pageable) {
        return customerOrderRepository.findByStatus(status, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersByCustomer(Long customerId, Pageable pageable) {
        return customerOrderRepository.findByCustomer(customerId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrdersWithoutDelivery(Pageable pageable) {
        return customerOrderRepository.findOrdersWithoutDelivery(pageable)
                .map(this::mapToResponseDTO);
    }

    public OrderResponseDTO updateOrderStatus(Long id, CustomerOrderStatus newStatus) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (customerOrder.getStatus() == CustomerOrderStatus.LIVREE) {
            throw new RuntimeException("Cannot modify delivered order");
        }

        customerOrder.setStatus(newStatus);
        CustomerOrder updatedOrder = customerOrderRepository.save(customerOrder);
        return mapToResponseDTO(updatedOrder);
    }

    public void deleteOrder(Long id) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (customerOrder.getStatus() == CustomerOrderStatus.EN_ROUTE || customerOrder.getStatus() == CustomerOrderStatus.LIVREE) {
            throw new RuntimeException("Cannot delete order that is shipped or delivered");
        }

        restoreProductStock(customerOrder.getOrderLines());
        customerOrderRepository.deleteById(id);
    }

    private void restoreProductStock(List<CustomerOrderLine> orderLines) {
        for (CustomerOrderLine orderLine : orderLines) {
            Product product = orderLine.getProduct();
            product.setStock(product.getStock() + orderLine.getQuantity());
            productRepository.save(product);
        }
    }

    private OrderResponseDTO mapToResponseDTO(CustomerOrder customerOrder) {
        OrderResponseDTO responseDTO = customerOrderMapper.toResponseDTO(customerOrder);

        if (customerOrder.getDelivery() != null) {
            responseDTO.setDelivery(deliveryMapper.toResponseDTO(customerOrder.getDelivery()));
        }

        return responseDTO;
    }
}