package org.supplychain.supplychain.controller.modelDelivery;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.response.ApiResponse;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "API pour gérer les clients")
public class CustomerController {

    private final ICustomerService customerService;

    @PostMapping
    @Operation(summary = "Créer un nouveau client", description = "Cette API permet de créer un client")
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(
            @Parameter(description = "Données du client à créer")
            @Valid @RequestBody CustomerDto dto) {
        ApiResponse<CustomerDto> response = customerService.createCustomer(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping
    @Operation(summary = "Lister les clients", description = "Récupérer la liste des clients avec pagination et filtre")
    public ResponseEntity<ApiResponse<Page<CustomerDto>>> getAllCustomers(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "2") int size,
            @Parameter(description = "Filtre par nom ou email") @RequestParam(required = false) String filter
    ) {
        ApiResponse<Page<CustomerDto>> response = customerService.getAllCustomers(page, size, filter);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client", description = "Modifier les informations d'un client par son ID")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(
            @Parameter(description = "ID du client") @PathVariable Long id,
            @Parameter(description = "Données mises à jour du client") @Valid @RequestBody CustomerDto dto) {
        ApiResponse<CustomerDto> response = customerService.updateCustomer(id, dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par ID", description = "Obtenir les détails d'un client")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        ApiResponse<CustomerDto> response = customerService.getCustomerById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client", description = "Supprimer un client par son ID")

    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        ApiResponse<Void> response = customerService.deleteCustomer(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
