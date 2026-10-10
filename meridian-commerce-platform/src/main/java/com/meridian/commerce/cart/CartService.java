package com.meridian.commerce.cart;

import com.meridian.commerce.catalog.Product;
import com.meridian.commerce.catalog.ProductRepository;
import com.meridian.commerce.common.exception.BusinessRuleException;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository carts;
    private final ProductRepository products;

    public CartService(CartRepository carts, ProductRepository products) {
        this.carts = carts;
        this.products = products;
    }

    @Transactional
    public Cart getOrCreate(Long customerId) {
        return carts.findFirstByCustomerIdAndCheckedOutFalse(customerId).orElseGet(() -> {
            Cart c = new Cart();
            c.setCustomerId(customerId);
            return carts.save(c);
        });
    }

    @Transactional
    public Cart addItem(Long customerId, Long productId, int quantity) {
        Product product = products.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        if (!product.isActive()) {
            throw new BusinessRuleException("PRODUCT_INACTIVE", "Product is no longer available");
        }
        Cart cart = getOrCreate(customerId);
        for (CartItem item : cart.getItems()) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                enforceMax(product, item.getQuantity());
                return cart;
            }
        }
        enforceMax(product, quantity);
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(productId);
        item.setSku(product.getSku());
        item.setQuantity(quantity);
        cart.getItems().add(item);
        return cart;
    }

    @Transactional
    public Cart updateQuantity(Long customerId, Long productId, int quantity) {
        Cart cart = getOrCreate(customerId);
        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));
        return cart;
    }

    @Transactional
    public Cart applyPromo(Long customerId, String code) {
        Cart cart = getOrCreate(customerId);
        cart.setPromoCode(code);
        return cart;
    }

    @Transactional
    public void clear(Long customerId) {
        Cart cart = getOrCreate(customerId);
        cart.getItems().clear();
        cart.setPromoCode(null);
    }

    private void enforceMax(Product product, int qty) {
        if (product.getMaxPerOrder() != null && qty > product.getMaxPerOrder()) {
            throw new BusinessRuleException("MAX_QTY_EXCEEDED",
                    "Maximum " + product.getMaxPerOrder() + " per order for " + product.getSku());
        }
    }
}
