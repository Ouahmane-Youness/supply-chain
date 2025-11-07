package org.supplychain.mysupply.livraison.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long idCustomer;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private Integer totalOrders;
}