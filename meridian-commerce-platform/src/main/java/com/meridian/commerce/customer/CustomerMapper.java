package com.meridian.commerce.customer;

import com.meridian.commerce.customer.dto.AddressDto;
import com.meridian.commerce.customer.dto.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getEmail(), c.getFirstName(), c.getLastName(), c.getPhone(),
                c.getTier(), c.getLoyaltyPoints(), c.getLifetimeSpend(), c.getMarketingOptIn(),
                c.getAddresses().stream().map(this::toDto).toList());
    }

    public AddressDto toDto(Address a) {
        return new AddressDto(a.getId(), a.getLine1(), a.getLine2(), a.getCity(), a.getState(),
                a.getPostalCode(), a.getCountry(), a.isDefaultAddress());
    }

    public Address toEntity(AddressDto dto) {
        Address a = new Address();
        a.setLine1(dto.line1());
        a.setLine2(dto.line2());
        a.setCity(dto.city());
        a.setState(dto.state());
        a.setPostalCode(dto.postalCode());
        a.setCountry(dto.country() == null ? "US" : dto.country());
        a.setDefaultAddress(dto.defaultAddress());
        return a;
    }
}
