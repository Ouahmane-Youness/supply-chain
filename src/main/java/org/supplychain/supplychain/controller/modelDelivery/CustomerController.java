package org.supplychain.supplychain.controller.modelDelivery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;
import org.springframework.data.domain.Page;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    @Operation(summary = "Créer un nouveau client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })


    @PostMapping
    public ResponseEntity<SuccessResponse<CustomerDto>> createCustomer(
            @RequestBody CustomerDto dto,
            HttpServletRequest request) {

        CustomerDto createdCustomer = customerService.createCustomer(dto);

        SuccessResponse<CustomerDto> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Client créé avec succès",
                createdCustomer,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




    @Operation(summary = "Récupérer la liste des clients avec pagination et filtre")
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<CustomerDto>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter,
            HttpServletRequest request) {

        Page<CustomerDto> customers = customerService.getAllCustomers(page, size, filter);

        SuccessResponse<Page<CustomerDto>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des clients récupérée avec succès",
                customers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer un client par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<CustomerDto>> getCustomerById(
            @PathVariable Long id,
            HttpServletRequest request) {

        CustomerDto customer = customerService.getCustomerById(id);

        SuccessResponse<CustomerDto> response = SuccessResponse.of(
                HttpStatus.OK,
                "Client récupéré avec succès",
                customer,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mettre à jour un client par son ID")
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<CustomerDto>> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDto dto,
            HttpServletRequest request) {

        CustomerDto updatedCustomer = customerService.updateCustomer(id, dto);

        SuccessResponse<CustomerDto> response = SuccessResponse.of(
                HttpStatus.OK,
                "Client mis à jour avec succès",
                updatedCustomer,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer un client par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteCustomer(
            @PathVariable Long id,
            HttpServletRequest request) {

        customerService.deleteCustomer(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Client supprimé avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}
