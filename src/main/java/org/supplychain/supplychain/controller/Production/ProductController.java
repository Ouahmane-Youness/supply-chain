package org.supplychain.supplychain.controller.Production;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.supplychain.supplychain.dto.product.ProductDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Production.Product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<SuccessResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            HttpServletRequest request) {

        ProductDTO createdProduct = productService.createProduct(productDTO);
        SuccessResponse<ProductDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Produit créé avec succès",
                createdProduct,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO,
            HttpServletRequest request) {

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        SuccessResponse<ProductDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produit mis à jour avec succès",
                updatedProduct,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteProduct(
            @PathVariable Long id,
            HttpServletRequest request) {

        productService.deleteProduct(id);
        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produit supprimé avec succès",
                null,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductDTO>> getProductById(
            @PathVariable Long id,
            HttpServletRequest request) {

        ProductDTO product = productService.getProductById(id);
        SuccessResponse<ProductDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produit récupéré avec succès",
                product,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            HttpServletRequest request) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productService.getAllProducts(pageable);

        SuccessResponse<Page<ProductDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des produits récupérée avec succès",
                products,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}
