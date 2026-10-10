package com.meridian.commerce.customer;

import com.meridian.commerce.common.api.PageResponse;
import com.meridian.commerce.customer.dto.AddressDto;
import com.meridian.commerce.customer.dto.CustomerResponse;
import com.meridian.commerce.customer.dto.UpdateCustomerRequest;
import com.meridian.commerce.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CurrentUser currentUser;

    public CustomerController(CustomerService customerService, CurrentUser currentUser) {
        this.customerService = customerService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPPORT','ADMIN')")
    public PageResponse<CustomerResponse> search(@RequestParam(required = false) String q,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "lastName") String sort) {
        return customerService.search(q, page, size, sort);
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        currentUser.assertCanAccessCustomer(id);
        return customerService.get(id);
    }

    @PatchMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @RequestBody UpdateCustomerRequest request) {
        currentUser.assertCanAccessCustomer(id);
        return customerService.update(id, request);
    }

    @PostMapping("/{id}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse addAddress(@PathVariable Long id, @Valid @RequestBody AddressDto address) {
        currentUser.assertCanAccessCustomer(id);
        return customerService.addAddress(id, address);
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAddress(@PathVariable Long id, @PathVariable Long addressId) {
        customerService.removeAddress(id, addressId);
    }
}
