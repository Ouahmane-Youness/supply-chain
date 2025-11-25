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
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;
import org.supplychain.mysupply.approvisionnement.mapper.RawMaterialMapper;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private RawMaterialMapper rawMaterialMapper;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    @Test
    void createRawMaterial_WhenValidData_ShouldCreateAndReturnMaterial() {
        RawMaterialDTO inputDTO = new RawMaterialDTO();
        inputDTO.setName("Aluminum");
        inputDTO.setDescription("High quality aluminum");
        inputDTO.setStock(50);
        inputDTO.setReservedStock(0);
        inputDTO.setStockMin(5);
        inputDTO.setUnit("kg");

        RawMaterial materialEntity = new RawMaterial();
        materialEntity.setName("Aluminum");
        materialEntity.setDescription("High quality aluminum");
        materialEntity.setStock(50);
        materialEntity.setReservedStock(0);
        materialEntity.setStockMin(5);
        materialEntity.setUnit("kg");

        RawMaterial savedMaterial = new RawMaterial();
        savedMaterial.setIdMaterial(1L);
        savedMaterial.setName("Aluminum");
        savedMaterial.setDescription("High quality aluminum");
        savedMaterial.setStock(50);
        savedMaterial.setReservedStock(0);
        savedMaterial.setStockMin(5);
        savedMaterial.setUnit("kg");

        RawMaterialResponseDTO responseDTO = new RawMaterialResponseDTO();
        responseDTO.setIdMaterial(1L);
        responseDTO.setName("Aluminum");
        responseDTO.setStock(50);

        when(rawMaterialRepository.existsByName("Aluminum")).thenReturn(false);
        when(rawMaterialMapper.toEntity(inputDTO)).thenReturn(materialEntity);
        when(rawMaterialRepository.save(materialEntity)).thenReturn(savedMaterial);
        when(rawMaterialMapper.toResponseDTO(savedMaterial)).thenReturn(responseDTO);

        RawMaterialResponseDTO result = rawMaterialService.createRawMaterial(inputDTO);

        assertThat(result).isNotNull();
        assertThat(result.getIdMaterial()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Aluminum");
        assertThat(result.getStock()).isEqualTo(50);

        verify(rawMaterialRepository).existsByName("Aluminum");
        verify(rawMaterialMapper).toEntity(inputDTO);
        verify(rawMaterialRepository).save(materialEntity);
        verify(rawMaterialMapper).toResponseDTO(savedMaterial);
    }

    @Test
    void createRawMaterial_WhenNameAlreadyExists_ShouldThrowException() {
        RawMaterialDTO inputDTO = new RawMaterialDTO();
        inputDTO.setName("Steel");
        inputDTO.setStock(100);
        inputDTO.setReservedStock(0);
        inputDTO.setStockMin(10);
        inputDTO.setUnit("kg");

        when(rawMaterialRepository.existsByName("Steel")).thenReturn(true);

        assertThatThrownBy(() -> rawMaterialService.createRawMaterial(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Material name already exists: Steel");

        verify(rawMaterialRepository).existsByName("Steel");
        verify(rawMaterialRepository, never()).save(any());
    }

    @Test
    void getRawMaterialById_WhenMaterialExists_ShouldReturnMaterial() {
        Long materialId = 1L;

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setIdMaterial(materialId);
        rawMaterial.setName("Steel");
        rawMaterial.setStock(100);
        rawMaterial.setStockMin(10);
        rawMaterial.setUnit("kg");

        RawMaterialResponseDTO responseDTO = new RawMaterialResponseDTO();
        responseDTO.setIdMaterial(materialId);
        responseDTO.setName("Steel");
        responseDTO.setStock(100);

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialMapper.toResponseDTO(rawMaterial)).thenReturn(responseDTO);

        RawMaterialResponseDTO result = rawMaterialService.getRawMaterialById(materialId);

        assertThat(result).isNotNull();
        assertThat(result.getIdMaterial()).isEqualTo(materialId);
        assertThat(result.getName()).isEqualTo("Steel");
        assertThat(result.getStock()).isEqualTo(100);

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialMapper).toResponseDTO(rawMaterial);
    }

    @Test
    void getRawMaterialById_WhenMaterialDoesNotExist_ShouldThrowException() {
        Long materialId = 999L;

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.getRawMaterialById(materialId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found with id: 999");

        verify(rawMaterialRepository).findById(materialId);
    }

    @Test
    void getAllRawMaterials_ShouldReturnPageOfMaterials() {
        Pageable pageable = PageRequest.of(0, 10);

        RawMaterial material1 = new RawMaterial();
        material1.setIdMaterial(1L);
        material1.setName("Steel");

        RawMaterial material2 = new RawMaterial();
        material2.setIdMaterial(2L);
        material2.setName("Aluminum");

        List<RawMaterial> materials = Arrays.asList(material1, material2);
        Page<RawMaterial> materialPage = new PageImpl<>(materials, pageable, materials.size());

        RawMaterialResponseDTO responseDTO1 = new RawMaterialResponseDTO();
        responseDTO1.setIdMaterial(1L);
        responseDTO1.setName("Steel");

        RawMaterialResponseDTO responseDTO2 = new RawMaterialResponseDTO();
        responseDTO2.setIdMaterial(2L);
        responseDTO2.setName("Aluminum");

        when(rawMaterialRepository.findAll(pageable)).thenReturn(materialPage);
        when(rawMaterialMapper.toResponseDTO(material1)).thenReturn(responseDTO1);
        when(rawMaterialMapper.toResponseDTO(material2)).thenReturn(responseDTO2);

        Page<RawMaterialResponseDTO> result = rawMaterialService.getAllRawMaterials(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Steel");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Aluminum");

        verify(rawMaterialRepository).findAll(pageable);
    }

    @Test
    void searchRawMaterials_WhenSearchTermProvided_ShouldReturnMatchingMaterials() {
        String searchTerm = "Steel";
        Pageable pageable = PageRequest.of(0, 10);

        RawMaterial material = new RawMaterial();
        material.setIdMaterial(1L);
        material.setName("Stainless Steel");

        List<RawMaterial> materials = Arrays.asList(material);
        Page<RawMaterial> materialPage = new PageImpl<>(materials, pageable, materials.size());

        RawMaterialResponseDTO responseDTO = new RawMaterialResponseDTO();
        responseDTO.setIdMaterial(1L);
        responseDTO.setName("Stainless Steel");

        when(rawMaterialRepository.findByNameContainingIgnoreCase(searchTerm, pageable)).thenReturn(materialPage);
        when(rawMaterialMapper.toResponseDTO(material)).thenReturn(responseDTO);

        Page<RawMaterialResponseDTO> result = rawMaterialService.searchRawMaterials(searchTerm, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Stainless Steel");

        verify(rawMaterialRepository).findByNameContainingIgnoreCase(searchTerm, pageable);
    }

    @Test
    void getLowStockMaterials_ShouldReturnMaterialsBelowMinStock() {
        Pageable pageable = PageRequest.of(0, 10);

        RawMaterial lowStockMaterial = new RawMaterial();
        lowStockMaterial.setIdMaterial(1L);
        lowStockMaterial.setName("Steel");
        lowStockMaterial.setStock(5);
        lowStockMaterial.setStockMin(10);

        List<RawMaterial> materials = Arrays.asList(lowStockMaterial);
        Page<RawMaterial> materialPage = new PageImpl<>(materials, pageable, materials.size());

        RawMaterialResponseDTO responseDTO = new RawMaterialResponseDTO();
        responseDTO.setIdMaterial(1L);
        responseDTO.setName("Steel");
        responseDTO.setStock(5);
        responseDTO.setLowStock(true);

        when(rawMaterialRepository.findLowStockMaterials(pageable)).thenReturn(materialPage);
        when(rawMaterialMapper.toResponseDTO(lowStockMaterial)).thenReturn(responseDTO);

        Page<RawMaterialResponseDTO> result = rawMaterialService.getLowStockMaterials(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).isLowStock()).isTrue();

        verify(rawMaterialRepository).findLowStockMaterials(pageable);
    }

    @Test
    void updateRawMaterial_WhenValidData_ShouldUpdateAndReturnMaterial() {
        Long materialId = 1L;

        RawMaterialDTO updateDTO = new RawMaterialDTO();
        updateDTO.setName("Updated Steel");
        updateDTO.setDescription("Updated description");
        updateDTO.setStock(200);
        updateDTO.setReservedStock(10);
        updateDTO.setStockMin(20);
        updateDTO.setUnit("kg");

        RawMaterial existingMaterial = new RawMaterial();
        existingMaterial.setIdMaterial(materialId);
        existingMaterial.setName("Old Steel");
        existingMaterial.setStock(100);

        RawMaterial updatedMaterial = new RawMaterial();
        updatedMaterial.setIdMaterial(materialId);
        updatedMaterial.setName("Updated Steel");
        updatedMaterial.setStock(200);

        RawMaterialResponseDTO responseDTO = new RawMaterialResponseDTO();
        responseDTO.setIdMaterial(materialId);
        responseDTO.setName("Updated Steel");
        responseDTO.setStock(200);

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
        when(rawMaterialRepository.existsByName("Updated Steel")).thenReturn(false);
        when(rawMaterialRepository.save(existingMaterial)).thenReturn(updatedMaterial);
        when(rawMaterialMapper.toResponseDTO(updatedMaterial)).thenReturn(responseDTO);

        RawMaterialResponseDTO result = rawMaterialService.updateRawMaterial(materialId, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Steel");
        assertThat(result.getStock()).isEqualTo(200);

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialRepository).existsByName("Updated Steel");
        verify(rawMaterialMapper).updateEntityFromDTO(updateDTO, existingMaterial);
        verify(rawMaterialRepository).save(existingMaterial);
    }

    @Test
    void updateRawMaterial_WhenMaterialNotFound_ShouldThrowException() {
        Long materialId = 999L;
        RawMaterialDTO updateDTO = new RawMaterialDTO();
        updateDTO.setName("Steel");

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.updateRawMaterial(materialId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found with id: 999");

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialRepository, never()).save(any());
    }

    @Test
    void updateRawMaterial_WhenNewNameAlreadyExists_ShouldThrowException() {
        Long materialId = 1L;

        RawMaterialDTO updateDTO = new RawMaterialDTO();
        updateDTO.setName("Aluminum");

        RawMaterial existingMaterial = new RawMaterial();
        existingMaterial.setIdMaterial(materialId);
        existingMaterial.setName("Steel");

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
        when(rawMaterialRepository.existsByName("Aluminum")).thenReturn(true);

        assertThatThrownBy(() -> rawMaterialService.updateRawMaterial(materialId, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Material name already exists: Aluminum");

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialRepository).existsByName("Aluminum");
        verify(rawMaterialRepository, never()).save(any());
    }

    @Test
    void updateStock_WhenValidData_ShouldUpdateStockAndRestockDate() {
        Long materialId = 1L;
        Integer newStock = 150;

        RawMaterial material = new RawMaterial();
        material.setIdMaterial(materialId);
        material.setName("Steel");
        material.setStock(100);

        RawMaterial updatedMaterial = new RawMaterial();
        updatedMaterial.setIdMaterial(materialId);
        updatedMaterial.setName("Steel");
        updatedMaterial.setStock(newStock);
        updatedMaterial.setLastRestockDate(LocalDate.now());

        RawMaterialResponseDTO responseDTO = new RawMaterialResponseDTO();
        responseDTO.setIdMaterial(materialId);
        responseDTO.setName("Steel");
        responseDTO.setStock(newStock);
        responseDTO.setLastRestockDate(LocalDate.now());

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(material));
        when(rawMaterialRepository.save(material)).thenReturn(updatedMaterial);
        when(rawMaterialMapper.toResponseDTO(updatedMaterial)).thenReturn(responseDTO);

        RawMaterialResponseDTO result = rawMaterialService.updateStock(materialId, newStock);

        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(newStock);
        assertThat(result.getLastRestockDate()).isEqualTo(LocalDate.now());
        assertThat(material.getStock()).isEqualTo(newStock);
        assertThat(material.getLastRestockDate()).isEqualTo(LocalDate.now());

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialRepository).save(material);
    }

    @Test
    void updateStock_WhenMaterialNotFound_ShouldThrowException() {
        Long materialId = 999L;
        Integer newStock = 150;

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.updateStock(materialId, newStock))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found with id: 999");

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialRepository, never()).save(any());
    }

    @Test
    void deleteRawMaterial_WhenMaterialExistsAndNotUsed_ShouldDelete() {
        Long materialId = 1L;

        when(rawMaterialRepository.existsById(materialId)).thenReturn(true);
        when(rawMaterialRepository.countSupplyOrderLinesByMaterialId(materialId)).thenReturn(0L);

        rawMaterialService.deleteRawMaterial(materialId);

        verify(rawMaterialRepository).existsById(materialId);
        verify(rawMaterialRepository).countSupplyOrderLinesByMaterialId(materialId);
        verify(rawMaterialRepository).deleteById(materialId);
    }

    @Test
    void deleteRawMaterial_WhenMaterialNotFound_ShouldThrowException() {
        Long materialId = 999L;

        when(rawMaterialRepository.existsById(materialId)).thenReturn(false);

        assertThatThrownBy(() -> rawMaterialService.deleteRawMaterial(materialId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found with id: 999");

        verify(rawMaterialRepository).existsById(materialId);
        verify(rawMaterialRepository, never()).deleteById(any());
    }

    @Test
    void deleteRawMaterial_WhenMaterialIsUsedInSupplyOrders_ShouldThrowException() {
        Long materialId = 1L;

        when(rawMaterialRepository.existsById(materialId)).thenReturn(true);
        when(rawMaterialRepository.countSupplyOrderLinesByMaterialId(materialId)).thenReturn(5L);

        assertThatThrownBy(() -> rawMaterialService.deleteRawMaterial(materialId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Cannot delete material that is used in supply orders");

        verify(rawMaterialRepository).existsById(materialId);
        verify(rawMaterialRepository).countSupplyOrderLinesByMaterialId(materialId);
        verify(rawMaterialRepository, never()).deleteById(any());
    }
}