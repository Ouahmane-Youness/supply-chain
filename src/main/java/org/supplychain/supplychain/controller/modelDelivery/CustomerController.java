package org.supplychain.supplychain.controller.modelDelivery;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.modelDelivery.CustomerDto;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    @PostMapping
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto customer = customerService.createCustomer(customerDto);
        return customer;
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }


}
