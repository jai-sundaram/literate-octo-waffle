package com.meridian.commerce.order;

import com.meridian.commerce.cart.Cart;
import com.meridian.commerce.cart.CartItem;
import com.meridian.commerce.cart.CartService;
import com.meridian.commerce.catalog.Product;
import com.meridian.commerce.catalog.ProductRepository;
import com.meridian.commerce.common.exception.BusinessRuleException;
import com.meridian.commerce.common.exception.ResourceNotFoundException;
import com.meridian.commerce.common.tenant.TenantContext;
import com.meridian.commerce.customer.Address;
import com.meridian.commerce.customer.Customer;
import com.meridian.commerce.customer.CustomerService;
import com.meridian.commerce.customer.LoyaltyService;
import com.meridian.commerce.inventory.InventoryService;
import com.meridian.commerce.inventory.Reservation;
import com.meridian.commerce.order.dto.PlaceOrderRequest;
import com.meridian.commerce.order.event.OrderCancelledEvent;
import com.meridian.commerce.order.event.OrderPlacedEvent;
import com.meridian.commerce.order.event.OrderShippedEvent;
import com.meridian.commerce.payment.PaymentService;
import com.meridian.commerce.pricing.CurrencyService;
import com.meridian.commerce.pricing.PriceBreakdown;
import com.meridian.commerce.pricing.PriceLine;
import com.meridian.commerce.pricing.PricingService;
import com.meridian.commerce.promotion.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orders;
    private final ProductRepository products;
    private final CartService cartService;
    private final CustomerService customerService;
    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final PromotionService promotionService;
    private final PaymentService paymentService;
    private final LoyaltyService loyaltyService;
    private final CurrencyService currencyService;
    private final OrderNumberGenerator numberGenerator;
    private final ApplicationEventPublisher events;

    public OrderService(OrderRepository orders, ProductRepository products, CartService cartService,
                        CustomerService customerService, InventoryService inventoryService,
                        PricingService pricingService, PromotionService promotionService,
                        PaymentService paymentService, LoyaltyService loyaltyService,
                        CurrencyService currencyService, OrderNumberGenerator numberGenerator,
                        ApplicationEventPublisher events) {
        this.orders = orders;
        this.products = products;
        this.cartService = cartService;
        this.customerService = customerService;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
        this.promotionService = promotionService;
        this.paymentService = paymentService;
        this.loyaltyService = loyaltyService;
        this.currencyService = currencyService;
        this.numberGenerator = numberGenerator;
        this.events = events;
    }

    /**
     * Checkout: converts the customer's open cart into an order, reserves stock, charges payment.
     */
    @Transactional
    public Order placeOrder(Long customerId, PlaceOrderRequest request) {
        Cart cart = cartService.getOrCreate(customerId);
        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleException("CART_EMPTY", "Cart is empty");
        }
        Customer customer = customerService.find(customerId);
        Address address = customer.getAddresses().stream()
                .filter(a -> a.getId().equals(request.shippingAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address", request.shippingAddressId()));

        Map<Long, Product> productById = products.findAllById(
                        cart.getItems().stream().map(CartItem::getProductId).toList())
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<PriceLine> priceLines = new ArrayList<>();
        Order order = new Order();
        order.setOrderNumber(numberGenerator.next());
        order.setTenantId(TenantContext.get());
        order.setCustomerId(customerId);
        order.setShippingAddress(toShippingAddress(customer, address));

        List<Reservation> allReservations = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product p = productById.get(item.getProductId());
            List<Reservation> res = inventoryService.reserve(p.getSku(), item.getQuantity());
            allReservations.addAll(res);

            OrderLine line = new OrderLine();
            line.setProductId(p.getId());
            line.setSku(p.getSku());
            line.setProductName(p.getName());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(p.getPrice());
            line.setLineTotal(p.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
            line.setAllocations(res.stream().map(r -> r.warehouseCode() + ":" + r.quantity())
                    .collect(Collectors.joining(",")));
            order.addLine(line);

            priceLines.add(new PriceLine(p.getSku(), item.getQuantity(), p.effectivePrice(),
                    p.getWeightGrams(), p.getTaxCategory()));
        }

        PriceBreakdown price = pricingService.price(priceLines, customer, cart.getPromoCode(), address.getState());
        order.setSubtotal(price.subtotal());
        order.setPromoDiscount(price.promoDiscount());
        order.setLoyaltyDiscount(price.loyaltyDiscount());
        order.setShipping(price.shipping());
        order.setTax(price.tax());
        order.setTotal(price.total());
        order.setPromoCode(cart.getPromoCode());

        String currency = request.currency() == null ? "USD" : request.currency().toUpperCase();
        if (!currencyService.isSupported(currency)) {
            throw new BusinessRuleException("CURRENCY_UNSUPPORTED", "Unsupported currency " + currency);
        }
        order.setCurrency(currency);
        order.setPlacedAt(Instant.now());
        orders.save(order);

        paymentService.charge(order.getId(), currencyService.fromUsd(order.getTotal(), currency), currency,
                request.paymentToken());
        order.setStatus(OrderStatus.PAID);

        if (price.appliedPromotionId() != null) {
            promotionService.recordUse(price.appliedPromotionId());
        }
        loyaltyService.award(customerId, order.getTotal());
        cart.setCheckedOut(true);

        events.publishEvent(new OrderPlacedEvent(order.getId(), order.getOrderNumber(), customerId));
        log.info("Order {} placed for customer {} total {}", order.getOrderNumber(), customerId, order.getTotal());
        return order;
    }

    public Order get(Long id) {
        return orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional(readOnly = true)
    public List<Order> listForCustomer(Long customerId, int page, int size) {
        return orders.findByCustomerId(customerId,
                org.springframework.data.domain.PageRequest.of(page, size)).getContent();
    }

    public Order cancel(Long orderId, String reason) {
        Order order = get(orderId);
        doCancel(order, reason);
        return order;
    }

    @Transactional
    private void doCancel(Order order, String reason) {
        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new BusinessRuleException("INVALID_TRANSITION",
                    "Cannot cancel order in status " + order.getStatus());
        }
        OrderStatus previous = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        inventoryService.release(reservationsOf(order));
        if (previous == OrderStatus.PAID || previous == OrderStatus.FULFILLING) {
            paymentService.refundFull(order.getId(), "Order cancelled: " + reason);
        }
        orders.save(order);
        events.publishEvent(new OrderCancelledEvent(order.getId(), order.getOrderNumber(),
                order.getCustomerId(), reason));
    }

    @Transactional
    public Order markShipped(Long orderId, String trackingNumber) {
        Order order = get(orderId);
        if (order.getStatus() != OrderStatus.FULFILLING && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessRuleException("INVALID_TRANSITION", "Order not ready to ship");
        }
        order.setStatus(OrderStatus.SHIPPED);
        order.setTrackingNumber(trackingNumber);
        order.setShippedAt(Instant.now());
        inventoryService.commit(reservationsOf(order));
        events.publishEvent(new OrderShippedEvent(order.getId(), order.getOrderNumber(),
                order.getCustomerId(), trackingNumber));
        return order;
    }

    @Transactional
    public Order transition(Long orderId, OrderStatus next) {
        Order order = get(orderId);
        if (!order.getStatus().canTransitionTo(next)) {
            throw new BusinessRuleException("INVALID_TRANSITION",
                    order.getStatus() + " -> " + next + " is not allowed");
        }
        order.setStatus(next);
        return order;
    }

    static List<Reservation> reservationsOf(Order order) {
        List<Reservation> result = new ArrayList<>();
        for (OrderLine line : order.getLines()) {
            if (line.getAllocations() == null || line.getAllocations().isBlank()) {
                continue;
            }
            for (String alloc : line.getAllocations().split(",")) {
                String[] parts = alloc.split(":");
                result.add(new Reservation(line.getSku(), parts[0], Integer.parseInt(parts[1])));
            }
        }
        return result;
    }

    private ShippingAddress toShippingAddress(Customer c, Address a) {
        ShippingAddress s = new ShippingAddress();
        s.setName(c.getFullName());
        s.setLine1(a.getLine1());
        s.setLine2(a.getLine2());
        s.setCity(a.getCity());
        s.setState(a.getState());
        s.setPostalCode(a.getPostalCode());
        return s;
    }
}
