package org.supplychain.supplychain.dto.rawmaterial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSummaryDTO {

    private Long id;

    private String name;

    private String contact;

    private String email;

    private String phone;

    private Double rating;

    private Integer leadTime;
}