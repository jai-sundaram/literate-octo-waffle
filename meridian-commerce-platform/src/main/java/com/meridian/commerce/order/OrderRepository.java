package com.meridian.commerce.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    @Query("select o from Order o where o.status = :status and o.placedAt < :before")
    List<Order> findStale(OrderStatus status, Instant before);

    @Query("select o from Order o where o.placedAt >= :from and o.placedAt < :to")
    List<Order> findPlacedBetween(Instant from, Instant to);
}
