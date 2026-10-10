package com.meridian.commerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findFirstByCustomerIdAndCheckedOutFalse(Long customerId);

    @Query("select c from Cart c where c.checkedOut = false and c.updatedAt < :cutoff")
    List<Cart> findAbandoned(Instant cutoff);
}
