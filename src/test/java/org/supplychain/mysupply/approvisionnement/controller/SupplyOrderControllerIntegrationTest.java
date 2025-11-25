package org.supplychain.mysupply.approvisionnement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderLineDTO;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.model.Supplier;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrder;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrderLine;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplierRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplyOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SupplyOrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplyOrderRepository supplyOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    private Supplier testSupplier;
    private RawMaterial testMaterial;

    @BeforeEach
    void setUp() {
        supplyOrderRepository.deleteAll();
        rawMaterialRepository.deleteAll();
        supplierRepository.deleteAll();

        testMaterial = new RawMaterial();
        testMaterial.setName("Steel");
        testMaterial.setStock(100);
        testMaterial.setReservedStock(0);
        testMaterial.setStockMin(10);
        testMaterial.setUnit("kg");
        testMaterial = rawMaterialRepository.save(testMaterial);

        testSupplier = new Supplier();
        testSupplier.setName("ABC Supplies");
        testSupplier.setContact("John Doe");
        testSupplier.setEmail("john@abc.com");
        testSupplier.setMaterials(new ArrayList<>(Arrays.asList(testMaterial)));
        testSupplier = supplierRepository.save(testSupplier);
    }

    @Test
    void createSupplyOrder_WhenValidData_ShouldReturn201AndCreatedOrder() throws Exception {
        SupplyOrderLineDTO lineDTO = new SupplyOrderLineDTO();
        lineDTO.setRawMaterialId(testMaterial.getIdMaterial());
        lineDTO.setQuantity(50);
        lineDTO.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrderDTO orderDTO = new SupplyOrderDTO();
        orderDTO.setOrderNumber("ORD-001");
        orderDTO.setSupplierId(testSupplier.getIdSupplier());
        orderDTO.setOrderDate(LocalDate.now());
        orderDTO.setOrderLines(Arrays.asList(lineDTO));

        mockMvc.perform(post("/api/supply-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idOrder").exists())
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.status").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.totalAmount").value(525.00))
                .andExpect(jsonPath("$.orderLines", hasSize(1)));
    }

    @Test
    void createSupplyOrder_WhenOrderNumberMissing_ShouldReturn400() throws Exception {
        SupplyOrderDTO orderDTO = new SupplyOrderDTO();
        orderDTO.setSupplierId(testSupplier.getIdSupplier());
        orderDTO.setOrderDate(LocalDate.now());

        mockMvc.perform(post("/api/supply-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSupplyOrder_WhenOrderNumberAlreadyExists_ShouldReturn400() throws Exception {
        SupplyOrder existingOrder = new SupplyOrder();
        existingOrder.setOrderNumber("ORD-DUPLICATE");
        existingOrder.setSupplier(testSupplier);
        existingOrder.setOrderDate(LocalDate.now());
        existingOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        existingOrder.setTotalAmount(BigDecimal.valueOf(1000));
        supplyOrderRepository.save(existingOrder);

        SupplyOrderLineDTO lineDTO = new SupplyOrderLineDTO();
        lineDTO.setRawMaterialId(testMaterial.getIdMaterial());
        lineDTO.setQuantity(50);
        lineDTO.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrderDTO orderDTO = new SupplyOrderDTO();
        orderDTO.setOrderNumber("ORD-DUPLICATE");
        orderDTO.setSupplierId(testSupplier.getIdSupplier());
        orderDTO.setOrderDate(LocalDate.now());
        orderDTO.setOrderLines(Arrays.asList(lineDTO));

        mockMvc.perform(post("/api/supply-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSupplyOrder_WhenSupplierNotFound_ShouldReturn404() throws Exception {
        SupplyOrderLineDTO lineDTO = new SupplyOrderLineDTO();
        lineDTO.setRawMaterialId(testMaterial.getIdMaterial());
        lineDTO.setQuantity(50);
        lineDTO.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrderDTO orderDTO = new SupplyOrderDTO();
        orderDTO.setOrderNumber("ORD-001");
        orderDTO.setSupplierId(999L);
        orderDTO.setOrderDate(LocalDate.now());
        orderDTO.setOrderLines(Arrays.asList(lineDTO));

        mockMvc.perform(post("/api/supply-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSupplyOrderById_WhenOrderExists_ShouldReturn200AndOrder() throws Exception {
        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-TEST");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        mockMvc.perform(get("/api/supply-orders/{id}", savedOrder.getIdOrder()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrder").value(savedOrder.getIdOrder()))
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST"))
                .andExpect(jsonPath("$.status").value("EN_ATTENTE"));
    }

    @Test
    void getSupplyOrderById_WhenOrderDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/supply-orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllSupplyOrders_ShouldReturn200AndListOfOrders() throws Exception {
        SupplyOrder order1 = new SupplyOrder();
        order1.setOrderNumber("ORD-001");
        order1.setSupplier(testSupplier);
        order1.setOrderDate(LocalDate.now());
        order1.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order1.setTotalAmount(BigDecimal.valueOf(1000));
        supplyOrderRepository.save(order1);

        SupplyOrder order2 = new SupplyOrder();
        order2.setOrderNumber("ORD-002");
        order2.setSupplier(testSupplier);
        order2.setOrderDate(LocalDate.now());
        order2.setStatus(SupplyOrderStatus.EN_COURS);
        order2.setTotalAmount(BigDecimal.valueOf(2000));
        supplyOrderRepository.save(order2);

        mockMvc.perform(get("/api/supply-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].orderNumber", containsInAnyOrder("ORD-001", "ORD-002")));
    }

    @Test
    void getSupplyOrdersByStatus_ShouldReturnOrdersWithGivenStatus() throws Exception {
        SupplyOrder order1 = new SupplyOrder();
        order1.setOrderNumber("ORD-001");
        order1.setSupplier(testSupplier);
        order1.setOrderDate(LocalDate.now());
        order1.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order1.setTotalAmount(BigDecimal.valueOf(1000));
        supplyOrderRepository.save(order1);

        SupplyOrder order2 = new SupplyOrder();
        order2.setOrderNumber("ORD-002");
        order2.setSupplier(testSupplier);
        order2.setOrderDate(LocalDate.now());
        order2.setStatus(SupplyOrderStatus.EN_COURS);
        order2.setTotalAmount(BigDecimal.valueOf(2000));
        supplyOrderRepository.save(order2);

        mockMvc.perform(get("/api/supply-orders/status/{status}", "EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-001"));
    }

    @Test
    void getSupplyOrdersBySupplier_ShouldReturnOrdersForGivenSupplier() throws Exception {
        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-001");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        supplyOrderRepository.save(order);

        mockMvc.perform(get("/api/supply-orders/supplier/{supplierId}", testSupplier.getIdSupplier()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].supplier.idSupplier").value(testSupplier.getIdSupplier()));
    }

    @Test
    void updateSupplyOrderStatus_WhenValidStatus_ShouldReturn200AndUpdatedOrder() throws Exception {
        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-001");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderLines(new ArrayList<>());
        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        mockMvc.perform(patch("/api/supply-orders/{id}/status", savedOrder.getIdOrder())
                        .param("status", "EN_COURS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EN_COURS"));
    }

    @Test
    void updateSupplyOrderStatus_WhenStatusIsRECUE_ShouldUpdateMaterialStock() throws Exception {
        SupplyOrderLine orderLine = new SupplyOrderLine();
        orderLine.setRawMaterial(testMaterial);
        orderLine.setQuantity(50);
        orderLine.setUnitPrice(BigDecimal.valueOf(10.50));

        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-001");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.EN_COURS);
        order.setTotalAmount(BigDecimal.valueOf(525));
        order.setOrderLines(new ArrayList<>(Arrays.asList(orderLine)));
        orderLine.setSupplyOrder(order);

        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        int initialStock = testMaterial.getStock();

        mockMvc.perform(patch("/api/supply-orders/{id}/status", savedOrder.getIdOrder())
                        .param("status", "RECUE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECUE"));

        RawMaterial updatedMaterial = rawMaterialRepository.findById(testMaterial.getIdMaterial()).orElseThrow();
        assert updatedMaterial.getStock() == initialStock + 50;
    }

    @Test
    void updateSupplyOrderStatus_WhenOrderAlreadyReceived_ShouldReturn403() throws Exception {
        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-001");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.RECUE);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderLines(new ArrayList<>());
        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        mockMvc.perform(patch("/api/supply-orders/{id}/status", savedOrder.getIdOrder())
                        .param("status", "EN_COURS"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteSupplyOrder_WhenOrderExistsAndNotReceived_ShouldReturn204() throws Exception {
        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-001");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        mockMvc.perform(delete("/api/supply-orders/{id}", savedOrder.getIdOrder()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSupplyOrder_WhenOrderNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/supply-orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSupplyOrder_WhenOrderIsReceived_ShouldReturn403() throws Exception {
        SupplyOrder order = new SupplyOrder();
        order.setOrderNumber("ORD-001");
        order.setSupplier(testSupplier);
        order.setOrderDate(LocalDate.now());
        order.setStatus(SupplyOrderStatus.RECUE);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        SupplyOrder savedOrder = supplyOrderRepository.save(order);

        mockMvc.perform(delete("/api/supply-orders/{id}", savedOrder.getIdOrder()))
                .andExpect(status().isForbidden());
    }
}