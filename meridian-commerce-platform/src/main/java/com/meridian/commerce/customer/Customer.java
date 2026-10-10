package com.meridian.commerce.customer;

import com.meridian.commerce.common.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private LoyaltyTier tier = LoyaltyTier.BRONZE;

    @Column(name = "loyalty_points")
    private int loyaltyPoints;

    @Column(name = "lifetime_spend", precision = 12, scale = 2)
    private BigDecimal lifetimeSpend = BigDecimal.ZERO;

    @Column(name = "marketing_opt_in")
    private Boolean marketingOptIn = Boolean.FALSE;

    @Column(name = "tax_exempt")
    private boolean taxExempt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LoyaltyTier getTier() { return tier; }
    public void setTier(LoyaltyTier tier) { this.tier = tier; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public BigDecimal getLifetimeSpend() { return lifetimeSpend; }
    public void setLifetimeSpend(BigDecimal lifetimeSpend) { this.lifetimeSpend = lifetimeSpend; }
    public Boolean getMarketingOptIn() { return marketingOptIn; }
    public void setMarketingOptIn(Boolean marketingOptIn) { this.marketingOptIn = marketingOptIn; }
    public boolean isTaxExempt() { return taxExempt; }
    public void setTaxExempt(boolean taxExempt) { this.taxExempt = taxExempt; }
    public List<Address> getAddresses() { return addresses; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
