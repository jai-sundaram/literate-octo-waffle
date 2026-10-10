package com.meridian.commerce.order;

import com.meridian.commerce.inventory.Reservation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceReservationParsingTest {

    @Test
    void parsesAllocationsFromLines() {
        Order order = new Order();
        OrderLine line = new OrderLine();
        line.setSku("APP-TEE-001");
        line.setAllocations("IAD1:2,DFW2:1");
        order.addLine(line);

        List<Reservation> res = OrderService.reservationsOf(order);

        assertThat(res).containsExactly(
                new Reservation("APP-TEE-001", "IAD1", 2),
                new Reservation("APP-TEE-001", "DFW2", 1));
    }
}
