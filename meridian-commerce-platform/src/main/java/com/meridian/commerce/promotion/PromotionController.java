package com.meridian.commerce.promotion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
public class PromotionController {

    private final PromotionRepository promotions;

    public PromotionController(PromotionRepository promotions) {
        this.promotions = promotions;
    }

    @GetMapping
    public List<Promotion> list() {
        return promotions.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Promotion create(@RequestBody Promotion promotion) {
        return promotions.save(promotion);
    }
}
