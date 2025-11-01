package org.supplychain.supplychain.controller.modelSupplier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.supplychain.supplychain.dto.supplier.SupplierDTO;
import org.supplychain.supplychain.service.modelSupplier.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.createSupplier(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
//        supplierService.deleteSupplier(id);
//        return ResponseEntity.noContent().build();
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build(); //delete
        } catch (RuntimeException e) {
            //message for the user
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSupplier(@RequestParam String name) {
        return ResponseEntity.ok(supplierService.searchSupplierByName(name));
    }
}
