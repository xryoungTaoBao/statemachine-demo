USE statemachine_demo;

-- ============================================================
--  Test data: 12 orders covering every state in the lifecycle
-- ============================================================

INSERT INTO t_order (
    order_no, user_id, product_name,
    quantity, amount,
    state, remark,
    payment_time, ship_time, receive_time, complete_time, cancel_time,
    cancel_reason, tracking_no, logistics_company, refund_reason, refund_amount,
    deleted, version, create_time, update_time
) VALUES

-- 1. PENDING – submitted, not yet paid
('ORD20240101001', 1001, 'Wireless Bluetooth Headphones',
 1, 299.00,
 'PENDING', 'Standard order',
 NULL, NULL, NULL, NULL, NULL,
 NULL, NULL, NULL, NULL, NULL,
 0, 0,
 DATE_SUB(NOW(), INTERVAL 10 MINUTE), DATE_SUB(NOW(), INTERVAL 10 MINUTE)),

-- 2. PENDING – about to time out
('ORD20240101002', 1002, 'Mechanical Keyboard RGB',
 1, 499.00,
 'PENDING', 'Discount applied',
 NULL, NULL, NULL, NULL, NULL,
 NULL, NULL, NULL, NULL, NULL,
 0, 0,
 DATE_SUB(NOW(), INTERVAL 28 MINUTE), DATE_SUB(NOW(), INTERVAL 28 MINUTE)),

-- 3. PAID – paid, warehouse picking
('ORD20240102001', 1003, '27-inch 4K Monitor',
 1, 1899.00,
 'PAID', NULL,
 DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NULL, NULL, NULL,
 NULL, NULL, NULL, NULL, NULL,
 0, 1,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),

-- 4. PAID – paid, express requested
('ORD20240102002', 1001, 'USB-C Hub 7-in-1',
 2, 298.00,
 'PAID', 'Express shipping requested',
 DATE_SUB(NOW(), INTERVAL 5 HOUR), NULL, NULL, NULL, NULL,
 NULL, NULL, NULL, NULL, NULL,
 0, 1,
 DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),

-- 5. SHIPPED – in transit
('ORD20240103001', 1004, 'Ergonomic Office Chair',
 1, 2499.00,
 'SHIPPED', 'Large item – 3-day delivery',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, NULL,
 NULL, 'SF1234567890', 'SF-EXPRESS', NULL, NULL,
 0, 2,
 DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 6. SHIPPED – in transit, auto-receive scheduled
('ORD20240103002', 1005, 'Smart Watch Series 9',
 1, 2199.00,
 'SHIPPED', NULL,
 DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), NULL, NULL, NULL,
 NULL, 'SF9876543210', 'SF-EXPRESS', NULL, NULL,
 0, 2,
 DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- 7. COMPLETED – normal happy path
('ORD20240104001', 1006, 'Gaming Mouse 25600 DPI',
 1, 399.00,
 'COMPLETED', NULL,
 DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY),
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL,
 NULL, 'YT0011223344', 'YTO-EXPRESS', NULL, NULL,
 0, 4,
 DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 8. COMPLETED – multiple items
('ORD20240104002', 1002, 'Laptop Stand Adjustable',
 2, 398.00,
 'COMPLETED', 'Gift wrap requested',
 DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY),
 DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), NULL,
 NULL, 'ZT5566778899', 'ZTO-EXPRESS', NULL, NULL,
 0, 4,
 DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- 9. CANCELLED – user cancelled before payment
('ORD20240105001', 1007, 'Portable SSD 1TB',
 1, 599.00,
 'CANCELLED', NULL,
 NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY),
 'Customer changed mind', NULL, NULL, NULL, NULL,
 0, 1,
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 10. CLOSED – payment timed out (system close)
('ORD20240105002', 1008, 'Noise Cancelling Earbuds',
 1, 899.00,
 'CLOSED', 'Payment timeout – auto-closed by system',
 NULL, NULL, NULL, NULL, NULL,
 NULL, NULL, NULL, NULL, NULL,
 0, 1,
 DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR)),

-- 11. REFUND_PENDING – buyer requested refund, awaiting review
('ORD20240106001', 1003, 'Wireless Bluetooth Headphones',
 1, 299.00,
 'REFUND_PENDING', 'Product defective – refund requested',
 DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY),
 DATE_SUB(NOW(), INTERVAL 12 DAY), NULL, NULL,
 NULL, 'SF1122334455', 'SF-EXPRESS', 'Product defective', 299.00,
 0, 4,
 DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- 12. CLOSED – refund completed (was REFUND_APPROVED -> CLOSED)
('ORD20240107001', 1009, 'Ergonomic Office Chair',
 1, 2499.00,
 'CLOSED', 'Wrong size – full refund approved',
 DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY),
 DATE_SUB(NOW(), INTERVAL 17 DAY), NULL, NULL,
 NULL, 'JD9988776655', 'JD-LOGISTICS', 'Wrong size', 2499.00,
 0, 5,
 DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY));

-- ============================================================
--  State history records
-- ============================================================

INSERT INTO t_order_state_history (
    order_id, order_no, from_state, to_state, event,
    event_type, operator_id, operator_name, remark, create_time
)
SELECT id, order_no,
       'CREATED', 'PENDING', 'SUBMIT',
       'NORMAL', user_id, NULL, 'Order submitted',
       create_time
FROM t_order;

-- Payment events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'PENDING', 'PAID', 'PAY', 'NORMAL', user_id, NULL, 'Payment confirmed', payment_time
FROM t_order WHERE state IN ('PAID','SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND payment_time IS NOT NULL;

-- Shipment events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'PAID', 'SHIPPED', 'SHIP', 'NORMAL', 10001, 'warehouse-system', 'Shipped by warehouse', ship_time
FROM t_order WHERE state IN ('SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND ship_time IS NOT NULL;

-- Receive events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'SHIPPED', 'RECEIVED', 'RECEIVE', 'NORMAL', user_id, NULL, 'Buyer confirmed receipt', receive_time
FROM t_order WHERE state IN ('RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND receive_time IS NOT NULL;

-- Complete events (RECEIVED -> COMPLETED)
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'RECEIVED', 'COMPLETED', 'COMPLETE', 'NORMAL', user_id, NULL, 'Order completed', complete_time
FROM t_order WHERE state = 'COMPLETED' AND complete_time IS NOT NULL;

-- Cancel events
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'PENDING', 'CANCELLED', 'CANCEL', 'NORMAL', user_id, NULL, cancel_reason, cancel_time
FROM t_order WHERE state = 'CANCELLED' AND cancel_time IS NOT NULL;

-- Refund request events (PAID -> REFUND_PENDING)
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'RECEIVED', 'REFUND_PENDING', 'REFUND', 'NORMAL', user_id, NULL, refund_reason, DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM t_order WHERE state IN ('REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED') AND refund_reason IS NOT NULL;

-- Refund complete events (REFUND_APPROVED -> CLOSED)
INSERT INTO t_order_state_history (order_id, order_no, from_state, to_state, event, event_type, operator_id, operator_name, remark, create_time)
SELECT id, order_no, 'REFUND_APPROVED', 'CLOSED', 'COMPLETE', 'NORMAL', 10000, 'system', 'Refund completed', update_time
FROM t_order WHERE state = 'CLOSED' AND refund_amount IS NOT NULL;

-- ============================================================
--  Event log records
-- ============================================================

INSERT INTO t_order_event_log (order_id, order_no, event, event_data, current_state, target_state, result, execution_time_ms, create_time)
SELECT id, order_no, 'SUBMIT',
       JSON_OBJECT('quantity', quantity, 'amount', amount),
       'CREATED', 'PENDING', 'ACCEPTED', 45, create_time
FROM t_order;

INSERT INTO t_order_event_log (order_id, order_no, event, event_data, current_state, target_state, result, execution_time_ms, create_time)
SELECT id, order_no, 'PAY',
       JSON_OBJECT('paymentMethod', 'ALIPAY', 'amount', amount),
       'PENDING', 'PAID', 'ACCEPTED', 120, payment_time
FROM t_order WHERE state IN ('PAID','SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND payment_time IS NOT NULL;

INSERT INTO t_order_event_log (order_id, order_no, event, event_data, current_state, target_state, result, execution_time_ms, create_time)
SELECT id, order_no, 'SHIP',
       JSON_OBJECT('trackingNo', tracking_no, 'carrier', logistics_company),
       'PAID', 'SHIPPED', 'ACCEPTED', 88, ship_time
FROM t_order WHERE state IN ('SHIPPED','RECEIVED','COMPLETED','REFUNDING','REFUND_PENDING','REFUND_APPROVED','REFUND_REJECTED','CLOSED') AND ship_time IS NOT NULL;
