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
import org.supplychain.mysupply.approvisionnement.dto.SupplierDTO;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.model.Supplier;
import org.supplychain.mysupply.approvisionnement.model.SupplyOrder;
import org.supplychain.mysupply.approvisionnement.repository.SupplierRepository;
import org.supplychain.mysupply.approvisionnement.repository.SupplyOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplyOrderRepository supplyOrderRepository;

    @BeforeEach
    void setUp() {
        supplyOrderRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    void createSupplier_WhenValidData_ShouldReturn201AndCreatedSupplier() throws Exception {
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName("ABC Supplies");
        supplierDTO.setContact("John Doe");
        supplierDTO.setEmail("john@abcsupplies.com");
        supplierDTO.setPhone("+1234567890");
        supplierDTO.setRating(4.5);
        supplierDTO.setLeadTime(7);

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSupplier").exists())
                .andExpect(jsonPath("$.name").value("ABC Supplies"))
                .andExpect(jsonPath("$.contact").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@abcsupplies.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"))
                .andExpect(jsonPath("$.rating").value(4.5))
                .andExpect(jsonPath("$.leadTime").value(7));
    }

    @Test
    void createSupplier_WhenEmailMissing_ShouldReturn400() throws Exception {
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName("ABC Supplies");
        supplierDTO.setContact("John Doe");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSupplier_WhenEmailInvalid_ShouldReturn400() throws Exception {
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName("ABC Supplies");
        supplierDTO.setContact("John Doe");
        supplierDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSupplier_WhenEmailAlreadyExists_ShouldReturn400() throws Exception {
        Supplier existingSupplier = new Supplier();
        existingSupplier.setName("Existing Supplier");
        existingSupplier.setContact("Jane Doe");
        existingSupplier.setEmail("existing@email.com");
        supplierRepository.save(existingSupplier);

        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName("New Supplier");
        supplierDTO.setContact("John Doe");
        supplierDTO.setEmail("existing@email.com");

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSupplierById_WhenSupplierExists_ShouldReturn200AndSupplier() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setName("XYZ Corp");
        supplier.setContact("Jane Smith");
        supplier.setEmail("jane@xyzcorp.com");
        supplier.setPhone("+9876543210");
        supplier.setRating(5.0);
        supplier.setLeadTime(5);
        Supplier savedSupplier = supplierRepository.save(supplier);

        mockMvc.perform(get("/api/suppliers/{id}", savedSupplier.getIdSupplier()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSupplier").value(savedSupplier.getIdSupplier()))
                .andExpect(jsonPath("$.name").value("XYZ Corp"))
                .andExpect(jsonPath("$.email").value("jane@xyzcorp.com"));
    }

    @Test
    void getSupplierById_WhenSupplierDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/suppliers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllSuppliers_ShouldReturn200AndListOfSuppliers() throws Exception {
        Supplier supplier1 = new Supplier();
        supplier1.setName("ABC Supplies");
        supplier1.setContact("John Doe");
        supplier1.setEmail("john@abc.com");
        supplierRepository.save(supplier1);

        Supplier supplier2 = new Supplier();
        supplier2.setName("XYZ Corp");
        supplier2.setContact("Jane Smith");
        supplier2.setEmail("jane@xyz.com");
        supplierRepository.save(supplier2);

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("ABC Supplies", "XYZ Corp")));
    }

    @Test
    void searchSuppliers_WhenSearchTermMatches_ShouldReturnMatchingSuppliers() throws Exception {
        Supplier supplier1 = new Supplier();
        supplier1.setName("ABC Supplies");
        supplier1.setContact("John Doe");
        supplier1.setEmail("john@abc.com");
        supplierRepository.save(supplier1);

        Supplier supplier2 = new Supplier();
        supplier2.setName("XYZ Corp");
        supplier2.setContact("Jane Smith");
        supplier2.setEmail("jane@xyz.com");
        supplierRepository.save(supplier2);

        mockMvc.perform(get("/api/suppliers/search")
                        .param("searchTerm", "ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("ABC Supplies"));
    }

    @Test
    void updateSupplier_WhenValidData_ShouldReturn200AndUpdatedSupplier() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setName("ABC Supplies");
        supplier.setContact("John Doe");
        supplier.setEmail("john@abc.com");
        supplier.setRating(4.0);
        supplier.setLeadTime(10);
        Supplier savedSupplier = supplierRepository.save(supplier);

        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("Updated ABC Supplies");
        updateDTO.setContact("John Updated");
        updateDTO.setEmail("john.updated@abc.com");
        updateDTO.setPhone("+1111111111");
        updateDTO.setRating(4.8);
        updateDTO.setLeadTime(6);

        mockMvc.perform(put("/api/suppliers/{id}", savedSupplier.getIdSupplier())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated ABC Supplies"))
                .andExpect(jsonPath("$.contact").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@abc.com"));
    }

    @Test
    void updateSupplier_WhenSupplierNotFound_ShouldReturn404() throws Exception {
        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("Updated Supplier");
        updateDTO.setContact("John Doe");
        updateDTO.setEmail("new@email.com");

        mockMvc.perform(put("/api/suppliers/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSupplier_WhenSupplierExistsAndNoActiveOrders_ShouldReturn204() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setName("ABC Supplies");
        supplier.setContact("John Doe");
        supplier.setEmail("john@abc.com");
        Supplier savedSupplier = supplierRepository.save(supplier);

        mockMvc.perform(delete("/api/suppliers/{id}", savedSupplier.getIdSupplier()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSupplier_WhenSupplierNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/suppliers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSupplier_WhenSupplierHasActiveOrders_ShouldReturn403() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setName("ABC Supplies");
        supplier.setContact("John Doe");
        supplier.setEmail("john@abc.com");
        Supplier savedSupplier = supplierRepository.save(supplier);

        SupplyOrder activeOrder = new SupplyOrder();
        activeOrder.setOrderNumber("ORD-001");
        activeOrder.setSupplier(savedSupplier);
        activeOrder.setOrderDate(LocalDate.now());
        activeOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        activeOrder.setTotalAmount(BigDecimal.valueOf(1000));
        supplyOrderRepository.save(activeOrder);

        mockMvc.perform(delete("/api/suppliers/{id}", savedSupplier.getIdSupplier()))
                .andExpect(status().isForbidden());
    }
}