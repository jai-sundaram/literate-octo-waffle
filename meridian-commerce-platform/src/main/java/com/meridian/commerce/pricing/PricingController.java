package com.meridian.commerce.pricing;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final CurrencyService currencyService;
    private final ShippingService shippingService;

    public PricingController(CurrencyService currencyService, ShippingService shippingService) {
        this.currencyService = currencyService;
        this.shippingService = shippingService;
    }

    @GetMapping("/convert")
    public Map<String, Object> convert(@RequestParam BigDecimal amount, @RequestParam String currency) {
        return Map.of("currency", currency, "amount", currencyService.fromUsd(amount, currency));
    }

    @GetMapping("/shipping-quote")
    public Map<String, Object> shipping(@RequestParam BigDecimal subtotal,
                                        @RequestParam int weightGrams,
                                        @RequestParam String state) {
        return Map.of("shipping", shippingService.quote(subtotal, weightGrams, state));
    }
}
