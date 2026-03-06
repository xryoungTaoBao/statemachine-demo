USE statemachine_demo;

-- ============================================================
--  Test data: 12 orders covering every state in the lifecycle
--
--  States used:
--    PENDING_PAYMENT  – created, awaiting payment
--    PENDING_SHIPMENT – paid, awaiting shipment
--    SHIPPED          – shipped, awaiting receipt
--    COMPLETED        – buyer confirmed receipt
--    CANCELLED        – order cancelled
--    REFUNDING        – refund requested, in progress
--    REFUNDED         – refund completed
-- ============================================================

INSERT INTO t_order (
    order_no, user_id, product_id, product_name,
    quantity, unit_price, total_amount,
    state, remark,
    payment_time, ship_time, receive_time, cancel_time, refund_time,
    timeout_at, deleted, version, create_time, update_time
) VALUES

-- 1. PENDING_PAYMENT – just placed, not yet paid
('ORD20240101001', 1001, 2001, 'Wireless Bluetooth Headphones',
 1, 299.00, 299.00,
 'PENDING_PAYMENT', 'Standard order',
 NULL, NULL, NULL, NULL, NULL,
 DATE_ADD(NOW(), INTERVAL 30 MINUTE), 0, 0,
 DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE)),

-- 2. PENDING_PAYMENT – about to time out
('ORD20240101002', 1002, 2002, 'Mechanical Keyboard RGB',
 1, 499.00, 499.00,
 'PENDING_PAYMENT', 'Discount applied',
 NULL, NULL, NULL, NULL, NULL,
 DATE_ADD(NOW(), INTERVAL 2 MINUTE), 0, 0,
 DATE_SUB(NOW(), INTERVAL 28 MINUTE), DATE_SUB(NOW(), INTERVAL 28 MINUTE)),

-- 3. PENDING_SHIPMENT – paid, warehouse picking
('ORD20240102001', 1003, 2003, '27-inch 4K Monitor',
 1, 1899.00, 1899.00,
 'PENDING_SHIPMENT', NULL,
 DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NULL, NULL, NULL,
 NULL, 0, 1,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),

-- 4. PENDING_SHIPMENT – paid, express requested
('ORD20240102002', 1001, 2004, 'USB-C Hub 7-in-1',
 2, 149.00, 298.00,
 'PENDING_SHIPMENT', 'Express shipping requested',
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

-- 10. CANCELLED – payment timed out (system cancel)
('ORD20240105002', 1008, 2010, 'Noise Cancelling Earbuds',
 1, 899.00, 899.00,
 'CANCELLED', 'Payment timeout – auto-cancelled by system',
 NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR), NULL,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), 0, 1,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),

-- 11. REFUNDING – buyer requested refund after receiving
('ORD20240106001', 1003, 2001, 'Wireless Bluetooth Headphones',
 1, 299.00, 299.00,
 'REFUNDING', 'Product defective – refund requested',
 DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY),
 DATE_SUB(NOW(), INTERVAL 12 DAY), NULL, NULL,
 NULL, 0, 4,
 DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 12. REFUNDED – refund completed
('ORD20240107001', 1009, 2005, 'Ergonomic Office Chair',
 1, 2499.00, 2499.00,
 'REFUNDED', 'Wrong size – full refund approved',
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
       'INITIAL',          'PENDING_PAYMENT', 'CREATE_ORDER',
       user_id,            'USER',            'Order created',
       create_time
FROM t_order;

-- Payment events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PENDING_PAYMENT', 'PENDING_SHIPMENT', 'PAY_ORDER', user_id, 'USER', 'Payment confirmed', payment_time
FROM t_order WHERE state IN ('PENDING_SHIPMENT','SHIPPED','COMPLETED','REFUNDING','REFUNDED') AND payment_time IS NOT NULL;

-- Shipment events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PENDING_SHIPMENT', 'SHIPPED', 'SHIP_ORDER', 10001, 'SYSTEM', 'Shipped by warehouse', ship_time
FROM t_order WHERE state IN ('SHIPPED','COMPLETED','REFUNDING','REFUNDED') AND ship_time IS NOT NULL;

-- Receive events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'SHIPPED', 'COMPLETED', 'CONFIRM_RECEIPT', user_id, 'USER', 'Buyer confirmed receipt', receive_time
FROM t_order WHERE state IN ('COMPLETED','REFUNDING','REFUNDED') AND receive_time IS NOT NULL;

-- Cancel events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'PENDING_PAYMENT', 'CANCELLED', 'CANCEL_ORDER',
       CASE WHEN remark LIKE '%system%' THEN 10000 ELSE user_id END,
       CASE WHEN remark LIKE '%system%' THEN 'SYSTEM' ELSE 'USER' END,
       remark, cancel_time
FROM t_order WHERE state = 'CANCELLED' AND cancel_time IS NOT NULL;

-- Refund request events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'COMPLETED', 'REFUNDING', 'REQUEST_REFUND', user_id, 'USER', 'Refund requested', DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM t_order WHERE state IN ('REFUNDING','REFUNDED');

-- Refund complete events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, operator_id, operator_type, remark, create_time)
SELECT id, order_no, 'REFUNDING', 'REFUNDED', 'COMPLETE_REFUND', 10000, 'SYSTEM', 'Refund completed', refund_time
FROM t_order WHERE state = 'REFUNDED' AND refund_time IS NOT NULL;

-- ============================================================
--  Matching event log records
-- ============================================================

INSERT INTO t_order_event_log (order_id, order_no, event, state_before, state_after, success, context_data, operator_id, operator_type, duration_ms, create_time)
SELECT id, order_no, 'CREATE_ORDER', 'INITIAL', 'PENDING_PAYMENT', 1,
       JSON_OBJECT('productId', product_id, 'quantity', quantity, 'totalAmount', total_amount),
       user_id, 'USER', 45, create_time
FROM t_order;

INSERT INTO t_order_event_log (order_id, order_no, event, state_before, state_after, success, context_data, operator_id, operator_type, duration_ms, create_time)
SELECT id, order_no, 'PAY_ORDER', 'PENDING_PAYMENT', 'PENDING_SHIPMENT', 1,
       JSON_OBJECT('paymentMethod', 'ALIPAY', 'totalAmount', total_amount),
       user_id, 'USER', 120, payment_time
FROM t_order WHERE state IN ('PENDING_SHIPMENT','SHIPPED','COMPLETED','REFUNDING','REFUNDED') AND payment_time IS NOT NULL;

INSERT INTO t_order_event_log (order_id, order_no, event, state_before, state_after, success, context_data, operator_id, operator_type, duration_ms, create_time)
SELECT id, order_no, 'SHIP_ORDER', 'PENDING_SHIPMENT', 'SHIPPED', 1,
       JSON_OBJECT('trackingNo', CONCAT('SF', FLOOR(RAND()*9000000000)+1000000000), 'carrier', 'SF-EXPRESS'),
       10001, 'SYSTEM', 88, ship_time
FROM t_order WHERE state IN ('SHIPPED','COMPLETED','REFUNDING','REFUNDED') AND ship_time IS NOT NULL;
