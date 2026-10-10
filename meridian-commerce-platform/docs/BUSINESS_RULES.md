# Business Rules (source of truth)

Approved by Product, Finance and Legal. Code must conform to this document. Last reviewed 2026-08-14.

## 1. API conventions
1.1 Pagination is **1-based**: `page=1` returns the first page. Max page size is 100.
1.2 Only whitelisted fields may be used for sorting.
1.3 Error responses must never expose stack traces, class names or internal SQL.
1.4 A missing resource returns 404, a business-rule violation returns 422, a permission problem returns 403,
    bad credentials return 401.

## 2. Customers & accounts
2.1 Emails are case-insensitive and stored lower-case. Login must accept any casing.
2.2 After **5** consecutive failed logins the account is locked. A successful login resets the counter.
2.3 Customer profile updates are **partial**: fields omitted (null) from the request are left unchanged.
2.4 A customer may read/modify only their own profile, addresses, orders and payments. Staff (SUPPORT, FINANCE,
    ADMIN) may access any customer.
2.5 Access tokens expire after `meridian.security.token-ttl-seconds` (default 1 hour).
2.6 Passwords, payment tokens and full card numbers must never be written to logs or the audit trail.

## 3. Loyalty
3.1 Customers earn 10 points per whole dollar of **merchandise spend after discounts** (excludes tax and shipping).
3.2 Tiers by points balance — boundaries are inclusive at the lower end:
    BRONZE 0–999, SILVER 1,000–4,999, GOLD 5,000–19,999, PLATINUM 20,000+.
3.3 Tier discounts: SILVER 2%, GOLD 5%, PLATINUM 8% of merchandise subtotal.
3.4 Refunds revoke the corresponding points and recompute the tier. Points never go below zero.

## 4. Pricing pipeline (checkout)
4.1 Merchandise subtotal uses each product's **effective price** (sale price if lower than list price).
    Order lines must show the same unit price that was charged.
4.2 Promo codes are case-insensitive. A promo is valid from `valid_from` through `valid_to` **inclusive**.
4.3 `min_order_amount` is met when the merchandise subtotal is **greater than or equal to** the minimum.
4.4 A promo's `max_uses` is a hard limit, including under concurrent checkouts.
4.5 Tax is computed on the **discounted** merchandise amount. Shipping is not taxed.
4.6 Grocery items are tax-exempt in CA, NY, TX, FL and WA. Tax-exempt customers pay no tax.
4.7 Standard shipping is free when the discounted merchandise subtotal is **≥ $75.00** (contiguous US only).
    Otherwise: $5.99 + $1.25 per **started** kilogram (1,001 g = 2 kg). AK/HI/PR/GU/VI add $15.00.
4.8 All money is computed with `BigDecimal`, rounded HALF_UP to cents at the end of each step.
4.9 FX rates are "foreign units per 1 USD". Converting USD→EUR multiplies by the rate.

## 5. Orders
5.1 Lifecycle: `PENDING_PAYMENT → PAID → FULFILLING → SHIPPED → DELIVERED`.
    Cancellation is allowed from PENDING_PAYMENT, PAID and FULFILLING only. **Shipped orders cannot be cancelled.**
    Refund is allowed from PAID and DELIVERED.
5.2 An order contains one line per distinct product in the cart; every cart line must appear on the order.
5.3 Order numbers are globally unique across all instances and restarts.
5.4 Checkout is atomic: if anything fails, no stock stays reserved, no promo use is recorded and the customer is
    **not charged**.
5.5 Confirmation emails are sent only for orders that were actually committed.
5.6 Cancelling an order releases its reservations and fully refunds any captured payment.
5.7 Orders left in PENDING_PAYMENT for 1 hour are auto-cancelled and their stock released.

## 6. Inventory
6.1 Stock is reserved across fulfilment centers in priority order (lowest number first), spilling over to the next
    center when one runs out.
6.2 Reserved quantity can never exceed on-hand, and neither may go negative — including under concurrency.
6.3 When an order ships, units move out of both `reserved` and `on_hand`.
6.4 `max_per_order` limits the total quantity of a product on a single order.

## 7. Payments & refunds
7.1 A charge must never be captured twice for the same order, including when the gateway times out and we retry.
7.2 Cumulative refunds may never exceed the captured amount. Refund amounts must be positive.
7.3 A fully refunded payment has status REFUNDED.

## 8. Reporting
8.1 Business days are defined in **America/New_York**.
8.2 Date ranges in reports are inclusive of both `from` and `to`.
8.3 Revenue reports must remain correct for daily volumes up to $500M.
8.4 CSV exports must be RFC 4180 compliant.
8.5 Reports are restricted to FINANCE and ADMIN.

## 9. Operations
9.1 Abandoned carts (no activity for 30 days) are purged once nightly at 02:00 UTC.
9.2 Expired promotions are deactivated nightly at 02:30 UTC.
9.3 Rate limit: 120 requests per client per minute; the counter resets every minute.
9.4 Tenant context (`X-Tenant-Id`) must never leak between requests.
