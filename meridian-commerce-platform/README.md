# Meridian Commerce Platform

Backend for Meridian Retail Group's direct-to-consumer storefront: catalog, carts, checkout,
inventory allocation across fulfilment centers, payments/refunds, loyalty, notifications and
finance reporting.

> **Status:** v4.12.0-SNAPSHOT. Owned by the Commerce Platform team (#commerce-platform).
> On-call runbook lives in Confluence → COMM → Runbooks.

## Stack

- Java 17, Spring Boot 3.3
- Spring Web, Data JPA (Hibernate 6), Security, Validation, Cache, AOP, Actuator
- H2 (local profile) / PostgreSQL 15 (prod profile)
- JUnit 5, Mockito, AssertJ

## Running locally

```bash
mvn spring-boot:run            # local profile, in-memory H2, seeded from data.sql
mvn test                       # unit + context tests
```

H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:meridian`, user `sa`).

Docker:

```bash
docker compose up --build      # app + postgres, prod profile
```

## Seed accounts (local only)

| Email                      | Password               | Role     | Customer ID |
|----------------------------|------------------------|----------|-------------|
| admin@meridian.example     | Admin!Meridian2026     | ADMIN    | —           |
| support@meridian.example   | Support!Meridian2026   | SUPPORT  | —           |
| finance@meridian.example   | Finance!Meridian2026   | FINANCE  | —           |
| jane.doe@example.com       | Customer!Meridian2026  | CUSTOMER | 1001        |
| marcus.lee@example.com     | Customer!Meridian2026  | CUSTOMER | 1002        |

Mock payment tokens: `tok_visa` (approved), `tok_declined`, `tok_insufficient`.

Promo codes: `FALL15`, `TENOFF`, `SHIPFREE`, `VIP100`.

## API overview

| Area       | Endpoint                                              | Who            |
|------------|-------------------------------------------------------|----------------|
| Auth       | `POST /api/auth/register`, `POST /api/auth/login`     | public         |
| Catalog    | `GET /api/products`, `GET /api/products/{id}`         | public         |
|            | `GET /api/categories/{slug}/products`                 | public         |
|            | `POST/PUT/DELETE /api/admin/products/**`              | ADMIN          |
| Customers  | `GET/PATCH /api/customers/{id}`, addresses            | self or staff  |
|            | `GET /api/customers?q=`                               | SUPPORT, ADMIN |
| Cart       | `GET/POST/PUT/DELETE /api/cart/**`                    | customer       |
| Orders     | `POST /api/orders`, `GET /api/orders[/{id}]`          | customer       |
|            | `POST /api/orders/{id}/cancel`                        | owner, SUPPORT |
|            | `POST /api/admin/orders/{id}/ship`, `/status`         | ADMIN          |
| Payments   | `GET /api/payments/order/{orderId}`                   | owner, staff   |
|            | `POST /api/payments/{id}/refunds`                     | SUPPORT, FINANCE, ADMIN |
| Inventory  | `GET /api/admin/inventory/{sku}`, `POST .../adjust`   | ADMIN          |
| Promotions | `GET/POST /api/admin/promotions`                      | ADMIN          |
| Reports    | `GET /api/reports/**`                                 | FINANCE, ADMIN |
| Pricing    | `GET /api/pricing/convert`, `/shipping-quote`         | authenticated  |

All list endpoints use **1-based** `page` parameters (see BUSINESS_RULES §1).

Sample requests are in `http/requests.http`.

## Project layout

```
src/main/java/com/meridian/commerce
├── audit          SOX audit trail (@Audited + aspect)
├── cart           shopping carts
├── catalog        products, categories
├── common         exceptions, utils, tenant context, web filters
├── config         Spring configuration + typed properties
├── customer       customers, addresses, loyalty
├── inventory      warehouses, stock, reservations, low-stock monitor
├── jobs           scheduled maintenance
├── notification   email + order event listeners
├── order          checkout, order lifecycle
├── payment        charges, refunds, gateway client
├── pricing        pricing pipeline, tax, shipping, FX
├── promotion      promo codes
├── reporting      finance reports + CSV export
└── security       token auth, users, roles
```

See `docs/ARCHITECTURE.md` and `docs/BUSINESS_RULES.md` before changing pricing, inventory or payments.
