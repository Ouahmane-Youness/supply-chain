package org.supplychain.mysupply.approvisionnement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderLineDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderResponseDTO;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;
import org.supplychain.mysupply.approvisionnement.mapper.SupplyOrderMapper;
import org.supplychain.mysupply.approvisionnement.mapper.SupplyOrderLineMapper;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplyOrderService {

    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderMapper supplyOrderMapper;
    private final SupplyOrderLineMapper supplyOrderLineMapper;

    public SupplyOrderResponseDTO createSupplyOrder(SupplyOrderDTO supplyOrderDTO) {
        if (supplyOrderRepository.existsByOrderNumber(supplyOrderDTO.getOrderNumber())) {
            throw new RuntimeException("Order number already exists: " + supplyOrderDTO.getOrderNumber());
        }

        Supplier supplier = supplierRepository.findById(supplyOrderDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplyOrderDTO.getSupplierId()));

        SupplyOrder supplyOrder = supplyOrderMapper.toEntity(supplyOrderDTO);
        supplyOrder.setSupplier(supplier);
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);

        List<SupplyOrderLine> orderLines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SupplyOrderLineDTO lineDTO : supplyOrderDTO.getOrderLines()) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(lineDTO.getRawMaterialId())
                    .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + lineDTO.getRawMaterialId()));

            SupplyOrderLine orderLine = supplyOrderLineMapper.toEntity(lineDTO);
            orderLine.setSupplyOrder(supplyOrder);
            orderLine.setRawMaterial(rawMaterial);

            BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            orderLines.add(orderLine);
        }

        supplyOrder.setOrderLines(orderLines);
        supplyOrder.setTotalAmount(totalAmount);

        SupplyOrder savedOrder = supplyOrderRepository.save(supplyOrder);
        return supplyOrderMapper.toResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public SupplyOrderResponseDTO getSupplyOrderById(Long id) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supply order not found with id: " + id));
        return supplyOrderMapper.toResponseDTO(supplyOrder);
    }

    @Transactional(readOnly = true)
    public Page<SupplyOrderResponseDTO> getAllSupplyOrders(Pageable pageable) {
        return supplyOrderRepository.findAll(pageable)
                .map(supplyOrderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<SupplyOrderResponseDTO> getSupplyOrdersByStatus(SupplyOrderStatus status, Pageable pageable) {
        return supplyOrderRepository.findByStatus(status, pageable)
                .map(supplyOrderMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<SupplyOrderResponseDTO> getSupplyOrdersBySupplier(Long supplierId, Pageable pageable) {
        return supplyOrderRepository.findBySupplier(supplierId, pageable)
                .map(supplyOrderMapper::toResponseDTO);
    }

    public SupplyOrderResponseDTO updateSupplyOrderStatus(Long id, SupplyOrderStatus newStatus) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supply order not found with id: " + id));

        if (supplyOrder.getStatus() == SupplyOrderStatus.RECUE) {
            throw new RuntimeException("Cannot modify received order");
        }

        supplyOrder.setStatus(newStatus);

        if (newStatus == SupplyOrderStatus.RECUE) {
            updateMaterialStock(supplyOrder);
        }

        SupplyOrder updatedOrder = supplyOrderRepository.save(supplyOrder);
        return supplyOrderMapper.toResponseDTO(updatedOrder);
    }

    private void updateMaterialStock(SupplyOrder supplyOrder) {
        for (SupplyOrderLine orderLine : supplyOrder.getOrderLines()) {
            RawMaterial material = orderLine.getRawMaterial();
            material.setStock(material.getStock() + orderLine.getQuantity());
            material.setLastRestockDate(LocalDate.now());
            rawMaterialRepository.save(material);
        }
    }

    public void deleteSupplyOrder(Long id) {
        SupplyOrder supplyOrder = supplyOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supply order not found with id: " + id));

        if (supplyOrder.getStatus() == SupplyOrderStatus.RECUE) {
            throw new RuntimeException("Cannot delete received order");
        }

        supplyOrderRepository.deleteById(id);
    }
}