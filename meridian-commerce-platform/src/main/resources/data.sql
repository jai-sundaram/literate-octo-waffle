-- Seed data for local development. Loaded after Hibernate creates the schema.

INSERT INTO categories (id, slug, name, parent_id) VALUES
  (1, 'apparel', 'Apparel', NULL),
  (2, 'footwear', 'Footwear', 1),
  (3, 'outdoor', 'Outdoor Gear', NULL),
  (4, 'pantry', 'Pantry', NULL),
  (5, 'digital', 'Digital Goods', NULL);

INSERT INTO products (id, sku, name, description, price, sale_price, weight_grams, category_id, tax_category, active, max_per_order, created_at, updated_at) VALUES
  (201, 'APP-TEE-001', 'Merino Crew Tee', 'Lightweight merino wool tee', 48.00, NULL, 180, 1, 'CLOTHING', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (202, 'APP-JKT-014', 'Ridgeline Rain Jacket', 'Waterproof 3-layer shell', 189.00, 149.00, 520, 1, 'CLOTHING', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (203, 'FTW-TRL-002', 'Trail Runner 2, Men''s', 'Neutral trail running shoe', 135.00, NULL, 1250, 2, 'CLOTHING', TRUE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (204, 'FTW-TRL-003', 'Trail Runner 2, Women''s', 'Neutral trail running shoe', 135.00, NULL, 1100, 2, 'CLOTHING', TRUE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (205, 'OUT-TNT-220', 'Summit 2P Tent', 'Two-person freestanding tent', 399.00, NULL, 2350, 3, 'STANDARD', TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (206, 'OUT-STV-010', 'Pocket Canister Stove', 'Ultralight canister stove', 59.95, NULL, 85, 3, 'STANDARD', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (207, 'OUT-HDL-300', 'Headlamp 300', '300 lumen rechargeable headlamp', 39.99, 29.99, 92, 3, 'STANDARD', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (208, 'PNT-COF-500', 'Organic Coffee Beans 500g', 'Single-origin, medium roast', 18.50, NULL, 520, 4, 'GROCERY', TRUE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (209, 'PNT-OAT-001', 'Trail Oats, Maple', 'Instant oats, 8 pack', 9.75, NULL, 400, 4, 'GROCERY', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (210, 'DIG-MAP-NP', 'National Parks Map Pack', 'Downloadable topo maps', 24.00, NULL, 0, 5, 'DIGITAL', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (211, 'LEGACY-0001', 'Classic Canteen (discontinued)', 'Steel canteen', 22.00, NULL, 450, 3, 'STANDARD', FALSE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO warehouses (code, name, state, priority, active) VALUES
  ('IAD1', 'Ashburn Fulfilment Center', 'VA', 1, TRUE),
  ('DFW2', 'Dallas Fulfilment Center', 'TX', 2, TRUE),
  ('RNO1', 'Reno Fulfilment Center', 'NV', 3, TRUE);

INSERT INTO inventory_items (id, sku, warehouse_code, on_hand, reserved, reorder_point) VALUES
  (1, 'APP-TEE-001', 'IAD1', 40, 0, 10),
  (2, 'APP-TEE-001', 'DFW2', 25, 0, 10),
  (3, 'APP-JKT-014', 'IAD1', 3, 0, 5),
  (4, 'APP-JKT-014', 'RNO1', 12, 0, 5),
  (5, 'FTW-TRL-002', 'IAD1', 6, 0, 4),
  (6, 'FTW-TRL-002', 'DFW2', 6, 0, 4),
  (7, 'FTW-TRL-003', 'DFW2', 9, 0, 4),
  (8, 'OUT-TNT-220', 'RNO1', 7, 0, 2),
  (9, 'OUT-STV-010', 'IAD1', 80, 0, 15),
  (10, 'OUT-HDL-300', 'IAD1', 2, 0, 10),
  (11, 'OUT-HDL-300', 'DFW2', 2, 0, 10),
  (12, 'OUT-HDL-300', 'RNO1', 30, 0, 10),
  (13, 'PNT-COF-500', 'IAD1', 200, 0, 40),
  (14, 'PNT-OAT-001', 'IAD1', 150, 0, 40),
  (15, 'DIG-MAP-NP', 'IAD1', 100000, 0, 0),
  (16, 'LEGACY-0001', 'IAD1', 1, 0, 5);

INSERT INTO customers (id, email, first_name, last_name, phone, tier, loyalty_points, lifetime_spend, marketing_opt_in, tax_exempt, created_at, updated_at) VALUES
  (1001, 'jane.doe@example.com', 'Jane', 'Doe', '+1-571-555-0142', 'SILVER', 4870, 487.00, TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1002, 'marcus.lee@example.com', 'Marcus', 'Lee', '+1-214-555-0199', 'GOLD', 19995, 1999.50, NULL, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1003, 'procurement@cascade-scouts.example', 'Cascade', 'Scouts', NULL, 'BRONZE', 0, 0.00, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO addresses (id, customer_id, line1, line2, city, state, postal_code, country, is_default) VALUES
  (5001, 1001, '44983 Knoll Square', NULL, 'Ashburn', 'VA', '20147', 'US', TRUE),
  (5002, 1001, '1 Market St', 'Suite 300', 'San Francisco', 'CA', '94105', 'US', FALSE),
  (5003, 1002, '2400 Victory Ave', NULL, 'Dallas', 'TX', '75219', 'US', TRUE),
  (5004, 1003, '100 Pike St', NULL, 'Seattle', 'WA', '98101', 'US', TRUE),
  (5005, 1002, '1 Aloha Tower Dr', NULL, 'Honolulu', 'HI', '96813', 'US', FALSE);

INSERT INTO user_accounts (id, email, password_hash, role, customer_id, department, enabled, failed_logins, created_at, updated_at) VALUES
  (1, 'admin@meridian.example', '$2a$10$0DeOPi/mhw1Y9YZCUs.drOoIUd85B1BnMcCo0i.BPtHVWJOU1L9C.', 'ADMIN', NULL, 'ENGINEERING', TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'support@meridian.example', '$2a$10$IkCtQDeSXhZLzvqoFJP03OqTfBxceRP7ulq5CtyJy/pPFesIx1pS2', 'SUPPORT', NULL, 'SUPPORT', TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'finance@meridian.example', '$2a$10$dAJwSPgpkMNEqGnCJqZqkeUMdZUZmCa3.pY8u2Y/TotafKOKWTu/C', 'FINANCE', NULL, 'FINANCE', TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 'jane.doe@example.com', '$2a$10$8jZWJatmYpZZv2Q9lcqsC.dF35n0CL259Mf7DseOazeZL9mpcH6tG', 'CUSTOMER', 1001, NULL, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (5, 'marcus.lee@example.com', '$2a$10$8jZWJatmYpZZv2Q9lcqsC.dF35n0CL259Mf7DseOazeZL9mpcH6tG', 'CUSTOMER', 1002, NULL, TRUE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO promotions (id, code, type, discount_value, min_order_amount, valid_from, valid_to, max_uses, used_count, stackable_with_loyalty, active) VALUES
  (1, 'FALL15', 'PERCENT', 15.00, 50.00, DATEADD('DAY', -10, CURRENT_DATE), DATEADD('DAY', 20, CURRENT_DATE), 1000, 0, FALSE, TRUE),
  (2, 'TENOFF', 'FIXED', 10.00, 40.00, DATEADD('DAY', -30, CURRENT_DATE), CURRENT_DATE, NULL, 0, TRUE, TRUE),
  (3, 'SHIPFREE', 'FREE_SHIPPING', 0.00, NULL, DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', 60, CURRENT_DATE), NULL, 0, TRUE, TRUE),
  (4, 'VIP100', 'FIXED', 100.00, 100.00, DATEADD('DAY', -1, CURRENT_DATE), DATEADD('DAY', 30, CURRENT_DATE), 1, 0, FALSE, TRUE);

ALTER TABLE categories ALTER COLUMN id RESTART WITH 100;
ALTER TABLE products ALTER COLUMN id RESTART WITH 300;
ALTER TABLE inventory_items ALTER COLUMN id RESTART WITH 100;
ALTER TABLE customers ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE addresses ALTER COLUMN id RESTART WITH 6000;
ALTER TABLE user_accounts ALTER COLUMN id RESTART WITH 100;
ALTER TABLE promotions ALTER COLUMN id RESTART WITH 100;
