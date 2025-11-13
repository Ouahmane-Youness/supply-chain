package org.supplychain.mysupply.approvisionnement.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialDTO;
import org.supplychain.mysupply.approvisionnement.dto.RawMaterialResponseDTO;
import org.supplychain.mysupply.approvisionnement.mapper.RawMaterialMapper;
import org.supplychain.mysupply.approvisionnement.model.RawMaterial;
import org.supplychain.mysupply.approvisionnement.repository.RawMaterialRepository;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {


    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private RawMaterialMapper rawMaterialMapper;

    @InjectMocks
    private RawMaterialService rawMaterialService;


    @Test
     void getRawMaterialById_WhenMaterialExists_ReturnMaterial() {
        Long materialId = 5000L;

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setIdMaterial(materialId);
        rawMaterial.setName("Steel");
        rawMaterial.setStock(100);
        rawMaterial.setStockMin(10);
        rawMaterial.setUnit("kg");

        RawMaterialResponseDTO rawMaterialResponseDTO = new RawMaterialResponseDTO();
        rawMaterialResponseDTO.setIdMaterial(materialId);
        rawMaterialResponseDTO.setName("Steel");
        rawMaterialResponseDTO.setStock(100);


        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialMapper.toResponseDTO(rawMaterial)).thenReturn(rawMaterialResponseDTO);

        RawMaterialResponseDTO result = rawMaterialService.getRawMaterialById(materialId);

        assertThat(result).isNotNull();
        assertThat(result.getIdMaterial()).isEqualTo(materialId);
        assertThat(result.getName()).isEqualTo("Steel");
        assertThat(result.getStock()).isEqualTo(100);

        verify(rawMaterialRepository).findById(materialId);
        verify(rawMaterialMapper).toResponseDTO(rawMaterial);

    }

    @Test
    void getRawMaterialById_WhenMaterialDoesNotExist_ShouldThrowException()
    {
        Long materialId = 999L;

        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.getRawMaterialById(materialId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Raw material not found with id: 999");

        verify(rawMaterialRepository).findById(materialId);
    }

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
        inputDTO.setName("Aluminum");
        inputDTO.setDescription("High quality aluminum");
        inputDTO.setStock(50);
        inputDTO.setReservedStock(0);
        inputDTO.setStockMin(5);
        inputDTO.setUnit("kg");

        when(rawMaterialRepository.existsByName("Aluminum")).thenReturn(true);

        assertThatThrownBy(()-> rawMaterialService.createRawMaterial(inputDTO)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Material name already exists: Aluminum");

        verify(rawMaterialRepository).existsByName("Aluminum");


    }

    }
