package com.meridian.commerce.customer;

import com.meridian.commerce.common.api.PageResponse;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import com.meridian.commerce.common.util.PageRequests;
import com.meridian.commerce.customer.dto.AddressDto;
import com.meridian.commerce.customer.dto.CustomerResponse;
import com.meridian.commerce.customer.dto.UpdateCustomerRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customers;
    private final CustomerMapper mapper;

    public CustomerService(CustomerRepository customers, CustomerMapper mapper) {
        this.customers = customers;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    public Customer find(Long id) {
        return customers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> search(String q, int page, int size, String sort) {
        return PageResponse.from(customers.search(q == null ? "" : q,
                PageRequests.of(page, size, sort, "ASC")).map(mapper::toResponse));
    }

    @Transactional
    public CustomerResponse update(Long id, UpdateCustomerRequest req) {
        Customer c = find(id);
        c.setEmail(req.email());
        c.setFirstName(req.firstName());
        c.setLastName(req.lastName());
        c.setPhone(req.phone());
        if (req.marketingOptIn() != null) {
            c.setMarketingOptIn(req.marketingOptIn());
        }
        return mapper.toResponse(c);
    }

    @Transactional
    public CustomerResponse addAddress(Long customerId, AddressDto dto) {
        Customer c = find(customerId);
        Address address = mapper.toEntity(dto);
        if (address.isDefaultAddress()) {
            for (Address existing : c.getAddresses()) {
                existing.setDefaultAddress(false);
            }
        }
        c.addAddress(address);
        return mapper.toResponse(c);
    }

    @Transactional
    public void removeAddress(Long customerId, Long addressId) {
        Customer c = find(customerId);
        for (Address a : c.getAddresses()) {
            if (a.getId().equals(addressId)) {
                c.getAddresses().remove(a);
            }
        }
    }
}
