package com.meridian.commerce.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCode(String code);

    @Query("select p from Promotion p where p.active = true and p.validTo < :today")
    List<Promotion> findExpired(LocalDate today);
}
