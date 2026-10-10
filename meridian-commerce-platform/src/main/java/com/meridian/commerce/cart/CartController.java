package com.meridian.commerce.cart;

import com.meridian.commerce.cart.dto.AddToCartRequest;
import com.meridian.commerce.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CurrentUser currentUser;

    public CartController(CartService cartService, CurrentUser currentUser) {
        this.cartService = cartService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public Cart get() {
        return cartService.getOrCreate(currentUser.get().customerId());
    }

    @PostMapping("/items")
    public Cart add(@Valid @RequestBody AddToCartRequest request) {
        return cartService.addItem(currentUser.get().customerId(), request.productId(), request.quantity());
    }

    @PutMapping("/items/{productId}")
    public Cart update(@PathVariable Long productId, @RequestParam int quantity) {
        return cartService.updateQuantity(currentUser.get().customerId(), productId, quantity);
    }

    @PostMapping("/promo")
    public Cart promo(@RequestParam String code) {
        return cartService.applyPromo(currentUser.get().customerId(), code);
    }

    @DeleteMapping
    public void clear() {
        cartService.clear(currentUser.get().customerId());
    }
}
