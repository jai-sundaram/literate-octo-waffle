# Architecture

Single deployable Spring Boot service, layered by feature package (controller → service → repository).

```
          ┌────────────┐   Bearer token    ┌──────────────────────────────┐
 client ─▶│ Filters    │──────────────────▶│ Controllers (REST, /api/**)  │
          │ correlation│                   └──────────────┬───────────────┘
          │ rate-limit │                                  │
          │ token auth │                   ┌──────────────▼───────────────┐
          └────────────┘                   │ Services (@Transactional)    │
                                           │  OrderService orchestrates:  │
                                           │  Cart → Inventory → Pricing  │
                                           │  → Payment → Loyalty → Event │
                                           └──────┬────────────────┬──────┘
                                                  │                │ ApplicationEvent
                                           ┌──────▼──────┐   ┌─────▼──────────────┐
                                           │ JPA repos   │   │ OrderEventListener │
                                           │ H2/Postgres │   │  → EmailSender     │
                                           └─────────────┘   └────────────────────┘
```

## Checkout sequence
1. Load open cart and customer, resolve shipping address.
2. Reserve stock per cart line (`InventoryService.reserve`) — allocations are stored on the order line
   as `WAREHOUSE:QTY,...` so they can be released or committed later.
3. Price the order (`PricingService`): promo → loyalty → shipping → tax.
4. Persist the order, charge the card (`PaymentService`), mark PAID.
5. Record promo use, award loyalty points, close the cart.
6. Publish `OrderPlacedEvent` → confirmation email.

## Cross-cutting
- **Security**: stateless HMAC bearer tokens (`TokenService`), roles CUSTOMER/SUPPORT/FINANCE/ADMIN.
- **Audit**: methods annotated `@Audited` are recorded by `AuditAspect` in `audit_log`.
- **Caching**: Spring simple cache for product lookups (`products`, `productsByCategory`).
- **Multi-tenancy**: `X-Tenant-Id` header → `TenantContext` (ThreadLocal), stamped onto orders.
- **Scheduling**: `MaintenanceJobs`, `LowStockMonitor`.
- **Payments**: `PaymentGatewayClient`; the local profile uses `MockPaymentGatewayClient`, which simulates
  the real processor's habit of timing out *after* capturing a charge.
