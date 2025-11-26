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
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class RawMaterialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @BeforeEach
    void setUp() {
        rawMaterialRepository.deleteAll();
    }

    @Test
    void createRawMaterial_WhenValidData_ShouldReturn201AndCreatedMaterial() throws Exception {
        RawMaterialDTO materialDTO = new RawMaterialDTO();
        materialDTO.setName("Steel");
        materialDTO.setDescription("High quality steel");
        materialDTO.setStock(100);
        materialDTO.setReservedStock(0);
        materialDTO.setStockMin(10);
        materialDTO.setUnit("kg");

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materialDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMaterial").exists())
                .andExpect(jsonPath("$.name").value("Steel"))
                .andExpect(jsonPath("$.description").value("High quality steel"))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.stockMin").value(10))
                .andExpect(jsonPath("$.unit").value("kg"));
    }

    @Test
    void createRawMaterial_WhenNameMissing_ShouldReturn400() throws Exception {
        RawMaterialDTO materialDTO = new RawMaterialDTO();
        materialDTO.setStock(100);
        materialDTO.setReservedStock(0);
        materialDTO.setStockMin(10);
        materialDTO.setUnit("kg");

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materialDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRawMaterial_WhenNameAlreadyExists_ShouldReturn400() throws Exception {
        RawMaterial existingMaterial = new RawMaterial();
        existingMaterial.setName("Aluminum");
        existingMaterial.setStock(50);
        existingMaterial.setReservedStock(0);
        existingMaterial.setStockMin(5);
        existingMaterial.setUnit("kg");
        rawMaterialRepository.save(existingMaterial);

        RawMaterialDTO materialDTO = new RawMaterialDTO();
        materialDTO.setName("Aluminum");
        materialDTO.setStock(100);
        materialDTO.setReservedStock(0);
        materialDTO.setStockMin(10);
        materialDTO.setUnit("kg");

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(materialDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRawMaterialById_WhenMaterialExists_ShouldReturn200AndMaterial() throws Exception {
        RawMaterial material = new RawMaterial();
        material.setName("Copper");
        material.setDescription("Pure copper");
        material.setStock(75);
        material.setReservedStock(5);
        material.setStockMin(15);
        material.setUnit("kg");
        RawMaterial savedMaterial = rawMaterialRepository.save(material);

        mockMvc.perform(get("/api/raw-materials/{id}", savedMaterial.getIdMaterial()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMaterial").value(savedMaterial.getIdMaterial()))
                .andExpect(jsonPath("$.name").value("Copper"))
                .andExpect(jsonPath("$.stock").value(75));
    }

    @Test
    void getRawMaterialById_WhenMaterialDoesNotExist_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/raw-materials/{id}", 999L))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllRawMaterials_ShouldReturn200AndListOfMaterials() throws Exception {
        RawMaterial material1 = new RawMaterial();
        material1.setName("Steel");
        material1.setStock(100);
        material1.setReservedStock(0);
        material1.setStockMin(10);
        material1.setUnit("kg");
        rawMaterialRepository.save(material1);

        RawMaterial material2 = new RawMaterial();
        material2.setName("Aluminum");
        material2.setStock(50);
        material2.setReservedStock(0);
        material2.setStockMin(5);
        material2.setUnit("kg");
        rawMaterialRepository.save(material2);

        mockMvc.perform(get("/api/raw-materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Steel", "Aluminum")));
    }

    @Test
    void searchRawMaterials_WhenSearchTermMatches_ShouldReturnMatchingMaterials() throws Exception {
        RawMaterial material1 = new RawMaterial();
        material1.setName("Stainless Steel");
        material1.setStock(100);
        material1.setReservedStock(0);
        material1.setStockMin(10);
        material1.setUnit("kg");
        rawMaterialRepository.save(material1);

        RawMaterial material2 = new RawMaterial();
        material2.setName("Aluminum");
        material2.setStock(50);
        material2.setReservedStock(0);
        material2.setStockMin(5);
        material2.setUnit("kg");
        rawMaterialRepository.save(material2);

        mockMvc.perform(get("/api/raw-materials/search")
                        .param("searchTerm", "Steel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Stainless Steel"));
    }

    @Test
    void getLowStockMaterials_ShouldReturnMaterialsBelowMinStock() throws Exception {
        RawMaterial lowStockMaterial = new RawMaterial();
        lowStockMaterial.setName("Steel");
        lowStockMaterial.setStock(5);
        lowStockMaterial.setReservedStock(0);
        lowStockMaterial.setStockMin(10);
        lowStockMaterial.setUnit("kg");
        rawMaterialRepository.save(lowStockMaterial);

        RawMaterial normalStockMaterial = new RawMaterial();
        normalStockMaterial.setName("Aluminum");
        normalStockMaterial.setStock(50);
        normalStockMaterial.setReservedStock(0);
        normalStockMaterial.setStockMin(5);
        normalStockMaterial.setUnit("kg");
        rawMaterialRepository.save(normalStockMaterial);

        mockMvc.perform(get("/api/raw-materials/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Steel"))
                .andExpect(jsonPath("$.content[0].lowStock").value(true));
    }

    @Test
    void updateRawMaterial_WhenValidData_ShouldReturn200AndUpdatedMaterial() throws Exception {
        RawMaterial material = new RawMaterial();
        material.setName("Steel");
        material.setStock(100);
        material.setReservedStock(0);
        material.setStockMin(10);
        material.setUnit("kg");
        RawMaterial savedMaterial = rawMaterialRepository.save(material);

        RawMaterialDTO updateDTO = new RawMaterialDTO();
        updateDTO.setName("Updated Steel");
        updateDTO.setDescription("Updated description");
        updateDTO.setStock(200);
        updateDTO.setReservedStock(10);
        updateDTO.setStockMin(20);
        updateDTO.setUnit("kg");

        mockMvc.perform(put("/api/raw-materials/{id}", savedMaterial.getIdMaterial())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Steel"))
                .andExpect(jsonPath("$.stock").value(200));
    }

    @Test
    void updateRawMaterial_WhenMaterialNotFound_ShouldReturn404() throws Exception {
        RawMaterialDTO updateDTO = new RawMaterialDTO();
        updateDTO.setName("Steel");
        updateDTO.setStock(100);
        updateDTO.setReservedStock(0);
        updateDTO.setStockMin(10);
        updateDTO.setUnit("kg");

        mockMvc.perform(put("/api/raw-materials/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStock_WhenValidData_ShouldReturn200AndUpdatedStock() throws Exception {
        RawMaterial material = new RawMaterial();
        material.setName("Steel");
        material.setStock(100);
        material.setReservedStock(0);
        material.setStockMin(10);
        material.setUnit("kg");
        RawMaterial savedMaterial = rawMaterialRepository.save(material);

        mockMvc.perform(patch("/api/raw-materials/{id}/stock", savedMaterial.getIdMaterial())
                        .param("newStock", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(150))
                .andExpect(jsonPath("$.lastRestockDate").exists());
    }

    @Test
    void deleteRawMaterial_WhenMaterialExists_ShouldReturn204() throws Exception {
        RawMaterial material = new RawMaterial();
        material.setName("Steel");
        material.setStock(100);
        material.setReservedStock(0);
        material.setStockMin(10);
        material.setUnit("kg");
        RawMaterial savedMaterial = rawMaterialRepository.save(material);

        mockMvc.perform(delete("/api/raw-materials/{id}", savedMaterial.getIdMaterial()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRawMaterial_WhenMaterialNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/raw-materials/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}