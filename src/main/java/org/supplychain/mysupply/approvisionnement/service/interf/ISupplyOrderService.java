package org.supplychain.mysupply.approvisionnement.service.interf;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderDTO;
import org.supplychain.mysupply.approvisionnement.dto.SupplyOrderResponseDTO;
import org.supplychain.mysupply.approvisionnement.enums.SupplyOrderStatus;

public interface ISupplyOrderService {

    SupplyOrderResponseDTO createSupplyOrder(SupplyOrderDTO supplyOrderDTO);

    SupplyOrderResponseDTO getSupplyOrderById(Long id);

    Page<SupplyOrderResponseDTO> getAllSupplyOrders(Pageable pageable);

    Page<SupplyOrderResponseDTO> getSupplyOrdersByStatus(SupplyOrderStatus status, Pageable pageable);

    Page<SupplyOrderResponseDTO> getSupplyOrdersBySupplier(Long supplierId, Pageable pageable);

    SupplyOrderResponseDTO updateSupplyOrderStatus(Long id, SupplyOrderStatus newStatus);

    void deleteSupplyOrder(Long id);
}