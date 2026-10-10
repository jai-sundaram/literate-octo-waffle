package com.meridian.commerce.cart;

import com.meridian.commerce.catalog.Product;
import com.meridian.commerce.catalog.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartServiceTest {

    @Test
    void addingSameProductTwiceMergesLine() {
        CartRepository carts = mock(CartRepository.class);
        ProductRepository products = mock(ProductRepository.class);
        Product p = new Product();
        ReflectionTestUtils.setField(p, "id", 7L);
        p.setSku("TEST-0007");
        p.setPrice(new BigDecimal("10.00"));
        when(products.findById(7L)).thenReturn(Optional.of(p));
        Cart cart = new Cart();
        when(carts.findFirstByCustomerIdAndCheckedOutFalse(any())).thenReturn(Optional.of(cart));

        CartService service = new CartService(carts, products);
        service.addItem(1L, 7L, 1);
        service.addItem(1L, 7L, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }
}
