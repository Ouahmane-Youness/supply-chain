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
import org.supplychain.mysupply.approvisionnement.dto.SupplierDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplierResponseDTO;
import org.supplychain.mysupply.approvisionnement.mapper.SupplierMapper;
import org.supplychain.mysupply.approvisionnement.model.Supplier;
import org.supplychain.mysupply.approvisionnement.repository.SupplierRepository;
import org.supplychain.mysupply.common.exception.ResourceNotFoundException;
import org.supplychain.mysupply.common.exception.UnauthorizedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    @Test
    void createSupplier_WhenValidData_ShouldCreateAndReturnSupplier() {
        SupplierDTO inputDTO = new SupplierDTO();
        inputDTO.setName("ABC Supplies");
        inputDTO.setContact("John Doe");
        inputDTO.setEmail("john@abcsupplies.com");
        inputDTO.setPhone("+1234567890");
        inputDTO.setRating(4.5);
        inputDTO.setLeadTime(7);

        Supplier supplierEntity = new Supplier();
        supplierEntity.setName("ABC Supplies");
        supplierEntity.setContact("John Doe");
        supplierEntity.setEmail("john@abcsupplies.com");
        supplierEntity.setPhone("+1234567890");
        supplierEntity.setRating(4.5);
        supplierEntity.setLeadTime(7);

        Supplier savedSupplier = new Supplier();
        savedSupplier.setIdSupplier(1L);
        savedSupplier.setName("ABC Supplies");
        savedSupplier.setContact("John Doe");
        savedSupplier.setEmail("john@abcsupplies.com");
        savedSupplier.setPhone("+1234567890");
        savedSupplier.setRating(4.5);
        savedSupplier.setLeadTime(7);

        SupplierResponseDTO responseDTO = new SupplierResponseDTO();
        responseDTO.setIdSupplier(1L);
        responseDTO.setName("ABC Supplies");
        responseDTO.setEmail("john@abcsupplies.com");

        when(supplierRepository.existsByEmail("john@abcsupplies.com")).thenReturn(false);
        when(supplierMapper.toEntity(inputDTO)).thenReturn(supplierEntity);
        when(supplierRepository.save(supplierEntity)).thenReturn(savedSupplier);
        when(supplierMapper.toResponseDTO(savedSupplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.createSupplier(inputDTO);

        assertThat(result).isNotNull();
        assertThat(result.getIdSupplier()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("ABC Supplies");
        assertThat(result.getEmail()).isEqualTo("john@abcsupplies.com");

        verify(supplierRepository).existsByEmail("john@abcsupplies.com");
        verify(supplierMapper).toEntity(inputDTO);
        verify(supplierRepository).save(supplierEntity);
        verify(supplierMapper).toResponseDTO(savedSupplier);
    }

    @Test
    void createSupplier_WhenEmailAlreadyExists_ShouldThrowException() {
        SupplierDTO inputDTO = new SupplierDTO();
        inputDTO.setName("XYZ Corp");
        inputDTO.setContact("Jane Smith");
        inputDTO.setEmail("existing@email.com");

        when(supplierRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.createSupplier(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists: existing@email.com");

        verify(supplierRepository).existsByEmail("existing@email.com");
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void getSupplierById_WhenSupplierExists_ShouldReturnSupplier() {
        Long supplierId = 1L;

        Supplier supplier = new Supplier();
        supplier.setIdSupplier(supplierId);
        supplier.setName("ABC Supplies");
        supplier.setContact("John Doe");
        supplier.setEmail("john@abcsupplies.com");
        supplier.setPhone("+1234567890");
        supplier.setRating(4.5);
        supplier.setLeadTime(7);

        SupplierResponseDTO responseDTO = new SupplierResponseDTO();
        responseDTO.setIdSupplier(supplierId);
        responseDTO.setName("ABC Supplies");
        responseDTO.setEmail("john@abcsupplies.com");

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.getSupplierById(supplierId);

        assertThat(result).isNotNull();
        assertThat(result.getIdSupplier()).isEqualTo(supplierId);
        assertThat(result.getName()).isEqualTo("ABC Supplies");
        assertThat(result.getEmail()).isEqualTo("john@abcsupplies.com");

        verify(supplierRepository).findById(supplierId);
        verify(supplierMapper).toResponseDTO(supplier);
    }

    @Test
    void getSupplierById_WhenSupplierDoesNotExist_ShouldThrowException() {
        Long supplierId = 999L;

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.getSupplierById(supplierId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: 999");

        verify(supplierRepository).findById(supplierId);
    }

    @Test
    void getAllSuppliers_ShouldReturnPageOfSuppliers() {
        Pageable pageable = PageRequest.of(0, 10);

        Supplier supplier1 = new Supplier();
        supplier1.setIdSupplier(1L);
        supplier1.setName("ABC Supplies");
        supplier1.setEmail("abc@supplies.com");

        Supplier supplier2 = new Supplier();
        supplier2.setIdSupplier(2L);
        supplier2.setName("XYZ Corp");
        supplier2.setEmail("xyz@corp.com");

        List<Supplier> suppliers = Arrays.asList(supplier1, supplier2);
        Page<Supplier> supplierPage = new PageImpl<>(suppliers, pageable, suppliers.size());

        SupplierResponseDTO responseDTO1 = new SupplierResponseDTO();
        responseDTO1.setIdSupplier(1L);
        responseDTO1.setName("ABC Supplies");

        SupplierResponseDTO responseDTO2 = new SupplierResponseDTO();
        responseDTO2.setIdSupplier(2L);
        responseDTO2.setName("XYZ Corp");

        when(supplierRepository.findAll(pageable)).thenReturn(supplierPage);
        when(supplierMapper.toResponseDTO(supplier1)).thenReturn(responseDTO1);
        when(supplierMapper.toResponseDTO(supplier2)).thenReturn(responseDTO2);

        Page<SupplierResponseDTO> result = supplierService.getAllSuppliers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("ABC Supplies");
        assertThat(result.getContent().get(1).getName()).isEqualTo("XYZ Corp");

        verify(supplierRepository).findAll(pageable);
    }

    @Test
    void searchSuppliers_WhenSearchTermProvided_ShouldReturnMatchingSuppliers() {
        String searchTerm = "ABC";
        Pageable pageable = PageRequest.of(0, 10);

        Supplier supplier = new Supplier();
        supplier.setIdSupplier(1L);
        supplier.setName("ABC Supplies");
        supplier.setContact("John Doe");

        List<Supplier> suppliers = Arrays.asList(supplier);
        Page<Supplier> supplierPage = new PageImpl<>(suppliers, pageable, suppliers.size());

        SupplierResponseDTO responseDTO = new SupplierResponseDTO();
        responseDTO.setIdSupplier(1L);
        responseDTO.setName("ABC Supplies");

        when(supplierRepository.findByNameOrContactContaining(searchTerm, pageable)).thenReturn(supplierPage);
        when(supplierMapper.toResponseDTO(supplier)).thenReturn(responseDTO);

        Page<SupplierResponseDTO> result = supplierService.searchSuppliers(searchTerm, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("ABC Supplies");

        verify(supplierRepository).findByNameOrContactContaining(searchTerm, pageable);
    }

    @Test
    void updateSupplier_WhenValidData_ShouldUpdateAndReturnSupplier() {
        Long supplierId = 1L;

        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("Updated ABC Supplies");
        updateDTO.setContact("Jane Doe");
        updateDTO.setEmail("jane@abcsupplies.com");
        updateDTO.setPhone("+9876543210");
        updateDTO.setRating(5.0);
        updateDTO.setLeadTime(5);

        Supplier existingSupplier = new Supplier();
        existingSupplier.setIdSupplier(supplierId);
        existingSupplier.setName("ABC Supplies");
        existingSupplier.setEmail("john@abcsupplies.com");

        Supplier updatedSupplier = new Supplier();
        updatedSupplier.setIdSupplier(supplierId);
        updatedSupplier.setName("Updated ABC Supplies");
        updatedSupplier.setEmail("jane@abcsupplies.com");

        SupplierResponseDTO responseDTO = new SupplierResponseDTO();
        responseDTO.setIdSupplier(supplierId);
        responseDTO.setName("Updated ABC Supplies");
        responseDTO.setEmail("jane@abcsupplies.com");

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByEmail("jane@abcsupplies.com")).thenReturn(false);
        when(supplierRepository.save(existingSupplier)).thenReturn(updatedSupplier);
        when(supplierMapper.toResponseDTO(updatedSupplier)).thenReturn(responseDTO);

        SupplierResponseDTO result = supplierService.updateSupplier(supplierId, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated ABC Supplies");
        assertThat(result.getEmail()).isEqualTo("jane@abcsupplies.com");

        verify(supplierRepository).findById(supplierId);
        verify(supplierRepository).existsByEmail("jane@abcsupplies.com");
        verify(supplierMapper).updateEntityFromDTO(updateDTO, existingSupplier);
        verify(supplierRepository).save(existingSupplier);
    }

    @Test
    void updateSupplier_WhenSupplierNotFound_ShouldThrowException() {
        Long supplierId = 999L;
        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("Updated Supplier");
        updateDTO.setEmail("new@email.com");

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.updateSupplier(supplierId, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: 999");

        verify(supplierRepository).findById(supplierId);
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void updateSupplier_WhenNewEmailAlreadyExists_ShouldThrowException() {
        Long supplierId = 1L;

        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("ABC Supplies");
        updateDTO.setEmail("existing@email.com");

        Supplier existingSupplier = new Supplier();
        existingSupplier.setIdSupplier(supplierId);
        existingSupplier.setName("ABC Supplies");
        existingSupplier.setEmail("old@email.com");

        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThatThrownBy(() -> supplierService.updateSupplier(supplierId, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists: existing@email.com");

        verify(supplierRepository).findById(supplierId);
        verify(supplierRepository).existsByEmail("existing@email.com");
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteSupplier_WhenSupplierExistsAndNoActiveOrders_ShouldDelete() {
        Long supplierId = 1L;

        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        when(supplierRepository.countActiveOrdersBySupplierId(supplierId)).thenReturn(0L);

        supplierService.deleteSupplier(supplierId);

        verify(supplierRepository).existsById(supplierId);
        verify(supplierRepository).countActiveOrdersBySupplierId(supplierId);
        verify(supplierRepository).deleteById(supplierId);
    }

    @Test
    void deleteSupplier_WhenSupplierNotFound_ShouldThrowException() {
        Long supplierId = 999L;

        when(supplierRepository.existsById(supplierId)).thenReturn(false);

        assertThatThrownBy(() -> supplierService.deleteSupplier(supplierId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: 999");

        verify(supplierRepository).existsById(supplierId);
        verify(supplierRepository, never()).deleteById(any());
    }

    @Test
    void deleteSupplier_WhenSupplierHasActiveOrders_ShouldThrowException() {
        Long supplierId = 1L;

        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        when(supplierRepository.countActiveOrdersBySupplierId(supplierId)).thenReturn(3L);

        assertThatThrownBy(() -> supplierService.deleteSupplier(supplierId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Cannot delete supplier with 3 active order(s)");

        verify(supplierRepository).existsById(supplierId);
        verify(supplierRepository).countActiveOrdersBySupplierId(supplierId);
        verify(supplierRepository, never()).deleteById(any());
    }
}