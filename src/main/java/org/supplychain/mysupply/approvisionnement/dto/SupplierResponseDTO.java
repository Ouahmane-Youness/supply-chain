package org.supplychain.mysupply.approvisionnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {

    private Long idSupplier;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private Double rating;
    private Integer leadTime;
}