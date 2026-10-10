package com.meridian.commerce.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void pendingCanBePaidOrCancelled() {
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.PAID)).isTrue();
        assertThat(OrderStatus.PENDING_PAYMENT.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
    }

    @Test
    void terminalStatesAreTerminal() {
        assertThat(OrderStatus.CANCELLED.allowedNext()).isEmpty();
        assertThat(OrderStatus.REFUNDED.allowedNext()).isEmpty();
    }

    @Test
    void deliveredCanOnlyBeRefunded() {
        assertThat(OrderStatus.DELIVERED.allowedNext()).containsExactly(OrderStatus.REFUNDED);
    }
}
