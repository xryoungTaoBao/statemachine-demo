USE statemachine_demo;

-- ============================================================
--  Test data: 12 orders covering every state in the lifecycle
--
--  States used:
--    CREATED          – order created, not yet submitted
--    PENDING          – submitted, awaiting payment
--    PAID             – paid, awaiting shipment
--    SHIPPED          – shipped, awaiting receipt
--    RECEIVED         – buyer signed for delivery
--    COMPLETED        – order fully complete
--    CANCELLED        – order cancelled
--    CLOSED           – closed (timeout / return / refund complete)
--    REFUNDING        – refund requested, in progress (sub-states: REFUND_PENDING, REFUND_APPROVED, REFUND_REJECTED)
-- ============================================================

INSERT INTO t_order (
    order_no, user_id, product_id, product_name,
    quantity, unit_price, total_amount,
    state, remark,
    payment_time, ship_time, receive_time, cancel_time, refund_time,
    timeout_at, deleted, version, create_time, update_time
) VALUES

-- 1. PENDING – submitted, not yet paid
('ORD20240101001', 1001, 2001, 'Wireless Bluetooth Headphones',
 1, 299.00, 299.00,
 'PENDING', 'Standard order',
 NULL, NULL, NULL, NULL, NULL,
 DATE_ADD(NOW(), INTERVAL 30 MINUTE), 0, 0,
 DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE)),

-- 2. PENDING – about to time out
('ORD20240101002', 1002, 2002, 'Mechanical Keyboard RGB',
 1, 499.00, 499.00,
 'PENDING', 'Discount applied',
 NULL, NULL, NULL, NULL, NULL,
 DATE_ADD(NOW(), INTERVAL 2 MINUTE), 0, 0,
 DATE_SUB(NOW(), INTERVAL 28 MINUTE), DATE_SUB(NOW(), INTERVAL 28 MINUTE)),

-- 3. PAID – paid, warehouse picking
('ORD20240102001', 1003, 2003, '27-inch 4K Monitor',
 1, 1899.00, 1899.00,
 'PAID', NULL,
 DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NULL, NULL, NULL,
 NULL, 0, 1,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),

-- 4. PAID – paid, express requested
('ORD20240102002', 1001, 2004, 'USB-C Hub 7-in-1',
 2, 149.00, 298.00,
 'PAID', 'Express shipping requested',
 DATE_SUB(NOW(), INTERVAL 5 HOUR), NULL, NULL, NULL, NULL,
 NULL, 0, 1,
 DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),

-- 5. SHIPPED – in transit
('ORD20240103001', 1004, 2005, 'Ergonomic Office Chair',
 1, 2499.00, 2499.00,
 'SHIPPED', 'Large item – 3-day delivery',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, NULL,
 NULL, 0, 2,
 DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 6. SHIPPED – in transit, auto-receive scheduled
('ORD20240103002', 1005, 2006, 'Smart Watch Series 9',
 1, 2199.00, 2199.00,
 'SHIPPED', NULL,
 DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), NULL, NULL, NULL,
 NULL, 0, 2,
 DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- 7. COMPLETED – normal happy path
('ORD20240104001', 1006, 2007, 'Gaming Mouse 25600 DPI',
 1, 399.00, 399.00,
 'COMPLETED', NULL,
 DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY),
 DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL,
 NULL, 0, 3,
 DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- 8. COMPLETED – multiple items
('ORD20240104002', 1002, 2008, 'Laptop Stand Adjustable',
 2, 199.00, 398.00,
 'COMPLETED', 'Gift wrap requested',
 DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY),
 DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, NULL,
 NULL, 0, 3,
 DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- 9. CANCELLED – user cancelled before payment
('ORD20240105001', 1007, 2009, 'Portable SSD 1TB',
 1, 599.00, 599.00,
 'CANCELLED', 'Customer changed mind',
 NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL,
 NULL, 0, 1,
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 10. CLOSED – payment timed out (system close)
('ORD20240105002', 1008, 2010, 'Noise Cancelling Earbuds',
 1, 899.00, 899.00,
 'CLOSED', 'Payment timeout – auto-closed by system',
 NULL, NULL, NULL, NULL, NULL,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), 0, 1,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),

-- 11. REFUND_PENDING – buyer requested refund, awaiting review
('ORD20240106001', 1003, 2001, 'Wireless Bluetooth Headphones',
 1, 299.00, 299.00,
 'REFUND_PENDING', 'Product defective – refund requested',
 DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY),
 DATE_SUB(NOW(), INTERVAL 12 DAY), NULL, NULL,
 NULL, 0, 4,
 DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 12. CLOSED – refund completed (was REFUND_APPROVED -> CLOSED)
('ORD20240107001', 1009, 2005, 'Ergonomic Office Chair',
 1, 2499.00, 2499.00,
 'CLOSED', 'Wrong size – full refund approved',
 DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY),
 DATE_SUB(NOW(), INTERVAL 17 DAY), NULL, DATE_SUB(NOW(), INTERVAL 15 DAY),
 NULL, 0, 5,
 DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY));

-- ============================================================
--  Matching state history records
-- ============================================================

INSERT INTO t_order_state_history (
    order_id, order_no, from_state, to_state, event,
    operator_id, operator_type, remark, create_time
)
SELECT id, order_no,
       'CREATED', 'PENDING', 'SUBMIT',
       user_id,   'USER',    'Order submitted',
       create_time
FROM t_order;

-- Payment events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PENDING', 'PAID', 'PAY', user_id, 'USER', 'Payment confirmed', payment_time
FROM t_order WHERE state IN ('PAID','SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND payment_time IS NOT NULL;

-- Shipment events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PAID', 'SHIPPED', 'SHIP', 10001, 'SYSTEM', 'Shipped by warehouse', ship_time
FROM t_order WHERE state IN ('SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND ship_time IS NOT NULL;

-- Receive events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'SHIPPED', 'RECEIVED', 'RECEIVE', user_id, 'USER', 'Buyer confirmed receipt', receive_time
FROM t_order WHERE state IN ('RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND receive_time IS NOT NULL;

-- Complete events (RECEIVED -> COMPLETED)
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'RECEIVED', 'COMPLETED', 'COMPLETE', user_id, 'USER', 'Order completed', receive_time
FROM t_order WHERE state = 'COMPLETED' AND receive_time IS NOT NULL;

-- Cancel events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PENDING', 'CANCELLED', 'CANCEL',
       CASE WHEN remark LIKE '%system%' THEN 10000 ELSE user_id END,
       CASE WHEN remark LIKE '%system%' THEN 'SYSTEM' ELSE 'USER' END,
       remark, cancel_time
FROM t_order WHERE state = 'CANCELLED' AND cancel_time IS NOT NULL;

-- Refund request events (PAID -> REFUNDING)
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PAID', 'REFUND_PENDING', 'REFUND', user_id, 'USER', 'Refund requested', DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM t_order WHERE state IN ('REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND receive_time IS NULL;

-- Refund complete events (REFUND_APPROVED -> CLOSED)
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'REFUND_APPROVED', 'CLOSED', 'COMPLETE', 10000, 'SYSTEM', 'Refund completed', refund_time
FROM t_order WHERE state = 'CLOSED' AND refund_time IS NOT NULL;

-- ============================================================
--  Matching event log records
-- ============================================================

INSERT INTO t_order_event_log (order_id, order_no, event, state_before, state_after, success, context_data, operator_id, operator_type, duration_ms, create_time)
SELECT id, order_no, 'SUBMIT', 'CREATED', 'PENDING', 1,
       JSON_OBJECT('productId', product_id, 'quantity', quantity, 'totalAmount', total_amount),
       user_id, 'USER', 45, create_time
FROM t_order;

INSERT INTO t_order_event_log (order_id, order_no, event, state_before, state_after, success, context_data, operator_id, operator_type, duration_ms, create_time)
SELECT id, order_no, 'PAY', 'PENDING', 'PAID', 1,
       JSON_OBJECT('paymentMethod', 'ALIPAY', 'totalAmount', total_amount),
       user_id, 'USER', 120, payment_time
FROM t_order WHERE state IN ('PAID','SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND payment_time IS NOT NULL;

INSERT INTO t_order_event_log (order_id, order_no, event, state_before, state_after, success, context_data, operator_id, operator_type, duration_ms, create_time)
SELECT id, order_no, 'SHIP', 'PAID', 'SHIPPED', 1,
       JSON_OBJECT('trackingNo', CONCAT('SF', FLOOR(RAND()*9000000000)+1000000000), 'carrier', 'SF-EXPRESS'),
       10001, 'SYSTEM', 88, ship_time
FROM t_order WHERE state IN ('SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND ship_time IS NOT NULL;
