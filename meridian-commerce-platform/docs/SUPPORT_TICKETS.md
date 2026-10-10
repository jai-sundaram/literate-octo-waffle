# Open support tickets (exported from ServiceDesk, 2026-10-08)

Triage these. Each one is a real customer- or ops-reported symptom; root causes are unknown.

| Ticket   | Reported by        | Symptom |
|----------|--------------------|---------|
| MCP-4101 | Customer (email)   | "I ordered a jacket, a tee and a stove. My confirmation only lists the jacket and I only got the jacket." Total charged was correct. |
| MCP-4102 | Finance            | Two charges on the same card for one order, roughly 1 in 10 orders during the gateway incident last week. |
| MCP-4103 | Customer (chat)    | Promo code `fall15` rejected as unknown, `FALL15` works. |
| MCP-4104 | Customer (chat)    | `TENOFF` says expired but the campaign page says it runs through today. |
| MCP-4105 | Finance            | Tax on discounted orders is higher than our tax provider calculates. |
| MCP-4106 | Customer (Twitter) | Order subtotal was exactly $75.00 but I was charged shipping. |
| MCP-4107 | Ops                | Warehouse IAD1 shows 3 Ridgeline jackets reserved but we have 0 open orders for it; RNO1 never gets jacket orders. |
| MCP-4108 | Ops                | "Reserved" counts keep climbing forever even after orders ship. |
| MCP-4109 | Customer (email)   | Updated my phone number in the app and my name disappeared from my account. |
| MCP-4110 | Security           | A customer account was able to call `GET /api/admin/promotions` and `GET /api/reports/daily`. |
| MCP-4111 | Security           | Pen-test: tokens issued in July still work. |
| MCP-4112 | Security           | Pen-test: `/api/reports/top-products?status=...` behaves strangely with quotes in the status. |
| MCP-4113 | Customer           | Jane (customer 1001) gets an error when viewing her own profile. Works for test users in staging (ids 1–50). |
| MCP-4114 | Customer           | Can't log in with `Jane.Doe@Example.com`; works lower-case. |
| MCP-4115 | Support            | Support agents get "Forbidden" when cancelling orders for customers. |
| MCP-4116 | Customer           | Cancelled my order before it shipped — got a 500 error, order still active. |
| MCP-4117 | Ops                | A shipped order was cancelled from the admin tool and the stock was "released" again. |
| MCP-4118 | Customer (email)   | Confirmation email says "Your total was MRD-20261003-00042 USD". |
| MCP-4119 | Customer           | Sometimes no confirmation email at all; nothing in the logs. |
| MCP-4120 | Finance            | Refunds on payment 88 totalled $412 on a $200 charge. |
| MCP-4121 | Finance            | Daily sales report for 2026-10-01 is missing late-evening orders and includes some from the next morning. |
| MCP-4122 | Finance            | `/api/reports/units-by-product` returns 500 on busy days. |
| MCP-4123 | Finance            | AOV endpoint returns 500 for most date ranges. |
| MCP-4124 | Finance            | CSV export opens in Excel with columns shifted on some rows. |
| MCP-4125 | Merchandising      | Category page 2 shows the same products as page 1; page 1 is missing the first products. |
| MCP-4126 | Merchandising      | Price changes in admin don't show on the product page until restart. |
| MCP-4127 | Customer           | Adding the same headlamp twice created two cart lines and let me exceed the per-order limit. |
| MCP-4128 | Customer           | `GET /api/cart` returns 500. |
| MCP-4129 | Customer           | Sale price was charged but my order lines show the full list price. |
| MCP-4130 | Customer           | `/api/pricing/convert?amount=100&currency=EUR` returns 108.70; EUR orders are charged the wrong amount. |
| MCP-4131 | Ops                | Abandoned-cart purge ran ~60 times last night; also deleted a cart a customer was actively using. |
| MCP-4132 | Ops                | Low stock monitor job crashes every run since we added the LEGACY SKU ignore list. |
| MCP-4133 | Ops                | Duplicate order number constraint violations after every deploy. |
| MCP-4134 | Security           | Logs contain plaintext passwords and payment tokens; audit_log table contains passwords too. |
| MCP-4135 | Security           | Browser clients get CORS errors on every call from the new storefront. |
| MCP-4136 | Ops                | Customer in tenant `meridian-ca` saw an order stamped with tenant `meridian-uk`. |
| MCP-4137 | Ops                | Rate limiter returns 429 to our monitoring probe permanently after ~2 minutes. |
| MCP-4138 | Customer           | Marcus has 19,995 points... went to exactly 20,000 and is still GOLD. |
| MCP-4139 | Customer           | Bad password on login returns HTTP 500 instead of 401. |
| MCP-4140 | Marketing          | `NotificationService.sendMarketing` throws NPE for customers who never set a marketing preference. |
| MCP-4141 | Customer           | Viewing an order (`GET /api/orders/{id}`) returns 500. Listing orders also fails. |
| MCP-4142 | Customer           | Coffee beans got taxed in California. |
