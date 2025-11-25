package org.supplychain.mysupply.approvisionnement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderLineDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderResponseDTO;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.mapper.SupplyOrderLineMapper;
import org.supplychain.mysupply.approvisionnement.mapper.SupplyOrderMapper;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.model.Supplier;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrder;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrderLine;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplierRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplyOrderRepository;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyOrderServiceTest {

    @Mock
    private SupplyOrderRepository supplyOrderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private SupplyOrderMapper supplyOrderMapper;

    @Mock
    private SupplyOrderLineMapper supplyOrderLineMapper;

    @InjectMocks
    private SupplyOrderService supplyOrderService;

    @Test
    void createSupplyOrder_WhenValidData_ShouldCreateAndReturnOrder() {
        SupplyOrderLineDTO lineDTO = new SupplyOrderLineDTO();
        lineDTO.setRawMaterialId(1L);
        lineDTO.setQuantity(100);
        lineDTO.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrderDTO inputDTO = new SupplyOrderDTO();
        inputDTO.setOrderNumber("ORD-001");
        inputDTO.setSupplierId(1L);
        inputDTO.setOrderDate(LocalDate.now());
        inputDTO.setOrderLines(Arrays.asList(lineDTO));

        RawMaterial material = new RawMaterial();
        material.setIdMaterial(1L);
        material.setName("Steel");

        Supplier supplier = new Supplier();
        supplier.setIdSupplier(1L);
        supplier.setName("ABC Supplies");
        supplier.setMaterials(Arrays.asList(material));

        SupplyOrder orderEntity = new SupplyOrder();
        orderEntity.setOrderNumber("ORD-001");
        orderEntity.setOrderDate(LocalDate.now());

        SupplyOrderLine orderLine = new SupplyOrderLine();
        orderLine.setQuantity(100);
        orderLine.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrder savedOrder = new SupplyOrder();
        savedOrder.setIdOrder(1L);
        savedOrder.setOrderNumber("ORD-001");
        savedOrder.setSupplier(supplier);
        savedOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        savedOrder.setTotalAmount(BigDecimal.valueOf(1050.00));

        SupplyOrderResponseDTO responseDTO = new SupplyOrderResponseDTO();
        responseDTO.setIdOrder(1L);
        responseDTO.setOrderNumber("ORD-001");
        responseDTO.setStatus(SupplyOrderStatus.EN_ATTENTE);

        when(supplyOrderRepository.existsByOrderNumber("ORD-001")).thenReturn(false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(supplyOrderMapper.toEntity(inputDTO)).thenReturn(orderEntity);
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(supplyOrderLineMapper.toEntity(lineDTO)).thenReturn(orderLine);
        when(supplyOrderRepository.save(any(SupplyOrder.class))).thenReturn(savedOrder);
        when(supplyOrderMapper.toResponseDTO(savedOrder)).thenReturn(responseDTO);

        SupplyOrderResponseDTO result = supplyOrderService.createSupplyOrder(inputDTO);

        assertThat(result).isNotNull();
        assertThat(result.getIdOrder()).isEqualTo(1L);
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(result.getStatus()).isEqualTo(SupplyOrderStatus.EN_ATTENTE);

        verify(supplyOrderRepository).existsByOrderNumber("ORD-001");
        verify(supplierRepository).findById(1L);
        verify(supplyOrderRepository).save(any(SupplyOrder.class));
    }

    @Test
    void createSupplyOrder_WhenOrderNumberAlreadyExists_ShouldThrowException() {
        SupplyOrderDTO inputDTO = new SupplyOrderDTO();
        inputDTO.setOrderNumber("ORD-001");
        inputDTO.setSupplierId(1L);

        when(supplyOrderRepository.existsByOrderNumber("ORD-001")).thenReturn(true);

        assertThatThrownBy(() -> supplyOrderService.createSupplyOrder(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order number already exists: ORD-001");

        verify(supplyOrderRepository).existsByOrderNumber("ORD-001");
        verify(supplyOrderRepository, never()).save(any());
    }

    @Test
    void createSupplyOrder_WhenSupplierNotFound_ShouldThrowException() {
        SupplyOrderDTO inputDTO = new SupplyOrderDTO();
        inputDTO.setOrderNumber("ORD-001");
        inputDTO.setSupplierId(999L);

        when(supplyOrderRepository.existsByOrderNumber("ORD-001")).thenReturn(false);
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplyOrderService.createSupplyOrder(inputDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: 999");

        verify(supplierRepository).findById(999L);
        verify(supplyOrderRepository, never()).save(any());
    }

    @Test
    void createSupplyOrder_WhenSupplierDoesNotProvideMaterial_ShouldThrowException() {
        SupplyOrderLineDTO lineDTO = new SupplyOrderLineDTO();
        lineDTO.setRawMaterialId(2L);
        lineDTO.setQuantity(100);
        lineDTO.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrderDTO inputDTO = new SupplyOrderDTO();
        inputDTO.setOrderNumber("ORD-001");
        inputDTO.setSupplierId(1L);
        inputDTO.setOrderDate(LocalDate.now());
        inputDTO.setOrderLines(Arrays.asList(lineDTO));

        RawMaterial material1 = new RawMaterial();
        material1.setIdMaterial(1L);
        material1.setName("Steel");

        RawMaterial material2 = new RawMaterial();
        material2.setIdMaterial(2L);
        material2.setName("Aluminum");

        Supplier supplier = new Supplier();
        supplier.setIdSupplier(1L);
        supplier.setName("ABC Supplies");
        supplier.setMaterials(Arrays.asList(material1));

        when(supplyOrderRepository.existsByOrderNumber("ORD-001")).thenReturn(false);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(rawMaterialRepository.findById(2L)).thenReturn(Optional.of(material2));

        assertThatThrownBy(() -> supplyOrderService.createSupplyOrder(inputDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Supplier 'ABC Supplies' does not provide the following material(s): Aluminum");

        verify(supplierRepository).findById(1L);
        verify(supplyOrderRepository, never()).save(any());
    }

    @Test
    void getSupplyOrderById_WhenOrderExists_ShouldReturnOrder() {
        Long orderId = 1L;

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(orderId);
        order.setOrderNumber("ORD-001");
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);

        SupplyOrderResponseDTO responseDTO = new SupplyOrderResponseDTO();
        responseDTO.setIdOrder(orderId);
        responseDTO.setOrderNumber("ORD-001");

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(supplyOrderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        SupplyOrderResponseDTO result = supplyOrderService.getSupplyOrderById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getIdOrder()).isEqualTo(orderId);
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");

        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderMapper).toResponseDTO(order);
    }

    @Test
    void getSupplyOrderById_WhenOrderDoesNotExist_ShouldThrowException() {
        Long orderId = 999L;

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplyOrderService.getSupplyOrderById(orderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supply order not found with id: 999");

        verify(supplyOrderRepository).findById(orderId);
    }

    @Test
    void getAllSupplyOrders_ShouldReturnPageOfOrders() {
        Pageable pageable = PageRequest.of(0, 10);

        SupplyOrder order1 = new SupplyOrder();
        order1.setIdOrder(1L);
        order1.setOrderNumber("ORD-001");

        SupplyOrder order2 = new SupplyOrder();
        order2.setIdOrder(2L);
        order2.setOrderNumber("ORD-002");

        List<SupplyOrder> orders = Arrays.asList(order1, order2);
        Page<SupplyOrder> orderPage = new PageImpl<>(orders, pageable, orders.size());

        SupplyOrderResponseDTO responseDTO1 = new SupplyOrderResponseDTO();
        responseDTO1.setIdOrder(1L);
        responseDTO1.setOrderNumber("ORD-001");

        SupplyOrderResponseDTO responseDTO2 = new SupplyOrderResponseDTO();
        responseDTO2.setIdOrder(2L);
        responseDTO2.setOrderNumber("ORD-002");

        when(supplyOrderRepository.findAll(pageable)).thenReturn(orderPage);
        when(supplyOrderMapper.toResponseDTO(order1)).thenReturn(responseDTO1);
        when(supplyOrderMapper.toResponseDTO(order2)).thenReturn(responseDTO2);

        Page<SupplyOrderResponseDTO> result = supplyOrderService.getAllSupplyOrders(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getOrderNumber()).isEqualTo("ORD-001");
        assertThat(result.getContent().get(1).getOrderNumber()).isEqualTo("ORD-002");

        verify(supplyOrderRepository).findAll(pageable);
    }

    @Test
    void getSupplyOrdersByStatus_ShouldReturnOrdersWithGivenStatus() {
        SupplyOrderStatus status = SupplyOrderStatus.EN_ATTENTE;
        Pageable pageable = PageRequest.of(0, 10);

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(1L);
        order.setOrderNumber("ORD-001");
        order.setStatus(status);

        List<SupplyOrder> orders = Arrays.asList(order);
        Page<SupplyOrder> orderPage = new PageImpl<>(orders, pageable, orders.size());

        SupplyOrderResponseDTO responseDTO = new SupplyOrderResponseDTO();
        responseDTO.setIdOrder(1L);
        responseDTO.setOrderNumber("ORD-001");
        responseDTO.setStatus(status);

        when(supplyOrderRepository.findByStatus(status, pageable)).thenReturn(orderPage);
        when(supplyOrderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        Page<SupplyOrderResponseDTO> result = supplyOrderService.getSupplyOrdersByStatus(status, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(status);

        verify(supplyOrderRepository).findByStatus(status, pageable);
    }

    @Test
    void getSupplyOrdersBySupplier_ShouldReturnOrdersForGivenSupplier() {
        Long supplierId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(1L);
        order.setOrderNumber("ORD-001");

        List<SupplyOrder> orders = Arrays.asList(order);
        Page<SupplyOrder> orderPage = new PageImpl<>(orders, pageable, orders.size());

        SupplyOrderResponseDTO responseDTO = new SupplyOrderResponseDTO();
        responseDTO.setIdOrder(1L);
        responseDTO.setOrderNumber("ORD-001");

        when(supplyOrderRepository.findBySupplier(supplierId, pageable)).thenReturn(orderPage);
        when(supplyOrderMapper.toResponseDTO(order)).thenReturn(responseDTO);

        Page<SupplyOrderResponseDTO> result = supplyOrderService.getSupplyOrdersBySupplier(supplierId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(supplyOrderRepository).findBySupplier(supplierId, pageable);
    }

    @Test
    void updateSupplyOrderStatus_WhenValidStatus_ShouldUpdateAndReturn() {
        Long orderId = 1L;
        SupplyOrderStatus newStatus = SupplyOrderStatus.EN_COURS;

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(orderId);
        order.setOrderNumber("ORD-001");
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setOrderLines(new ArrayList<>());

        SupplyOrder updatedOrder = new SupplyOrder();
        updatedOrder.setIdOrder(orderId);
        updatedOrder.setOrderNumber("ORD-001");
        updatedOrder.setStatus(newStatus);

        SupplyOrderResponseDTO responseDTO = new SupplyOrderResponseDTO();
        responseDTO.setIdOrder(orderId);
        responseDTO.setStatus(newStatus);

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(supplyOrderRepository.save(order)).thenReturn(updatedOrder);
        when(supplyOrderMapper.toResponseDTO(updatedOrder)).thenReturn(responseDTO);

        SupplyOrderResponseDTO result = supplyOrderService.updateSupplyOrderStatus(orderId, newStatus);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(newStatus);

        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository).save(order);
    }

    @Test
    void updateSupplyOrderStatus_WhenStatusIsRECUE_ShouldUpdateMaterialStock() {
        Long orderId = 1L;
        SupplyOrderStatus newStatus = SupplyOrderStatus.RECUE;

        RawMaterial material = new RawMaterial();
        material.setIdMaterial(1L);
        material.setName("Steel");
        material.setStock(100);

        SupplyOrderLine orderLine = new SupplyOrderLine();
        orderLine.setRawMaterial(material);
        orderLine.setQuantity(50);

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(orderId);
        order.setOrderNumber("ORD-001");
        order.setStatus(SupplyOrderStatus.EN_COURS);
        order.setOrderLines(Arrays.asList(orderLine));

        SupplyOrder updatedOrder = new SupplyOrder();
        updatedOrder.setIdOrder(orderId);
        updatedOrder.setStatus(newStatus);

        SupplyOrderResponseDTO responseDTO = new SupplyOrderResponseDTO();
        responseDTO.setIdOrder(orderId);
        responseDTO.setStatus(newStatus);

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(supplyOrderRepository.save(order)).thenReturn(updatedOrder);
        when(supplyOrderMapper.toResponseDTO(updatedOrder)).thenReturn(responseDTO);

        SupplyOrderResponseDTO result = supplyOrderService.updateSupplyOrderStatus(orderId, newStatus);

        assertThat(result).isNotNull();
        assertThat(material.getStock()).isEqualTo(150);
        assertThat(material.getLastRestockDate()).isEqualTo(LocalDate.now());

        verify(supplyOrderRepository).findById(orderId);
        verify(rawMaterialRepository).save(material);
        verify(supplyOrderRepository).save(order);
    }

    @Test
    void updateSupplyOrderStatus_WhenOrderAlreadyReceived_ShouldThrowException() {
        Long orderId = 1L;
        SupplyOrderStatus newStatus = SupplyOrderStatus.EN_COURS;

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(orderId);
        order.setStatus(SupplyOrderStatus.RECUE);

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> supplyOrderService.updateSupplyOrderStatus(orderId, newStatus))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Cannot modify received order");

        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository, never()).save(any());
    }

    @Test
    void deleteSupplyOrder_WhenOrderExistsAndNotReceived_ShouldDelete() {
        Long orderId = 1L;

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(orderId);
        order.setOrderNumber("ORD-001");
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        supplyOrderService.deleteSupplyOrder(orderId);

        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository).deleteById(orderId);
    }

    @Test
    void deleteSupplyOrder_WhenOrderNotFound_ShouldThrowException() {
        Long orderId = 999L;

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplyOrderService.deleteSupplyOrder(orderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supply order not found with id: 999");

        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository, never()).deleteById(any());
    }

    @Test
    void deleteSupplyOrder_WhenOrderIsReceived_ShouldThrowException() {
        Long orderId = 1L;

        SupplyOrder order = new SupplyOrder();
        order.setIdOrder(orderId);
        order.setStatus(SupplyOrderStatus.RECUE);

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> supplyOrderService.deleteSupplyOrder(orderId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Cannot delete received order");

        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository, never()).deleteById(any());
    }
}