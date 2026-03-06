-- ============================================================
--  Schema: statemachine_demo
--  Spring Statemachine Order Management Demo
-- ============================================================

DROP DATABASE IF EXISTS statemachine_demo;
CREATE DATABASE statemachine_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE statemachine_demo;

-- ------------------------------------------------------------
--  Table: t_order
--  Core order table tracking lifecycle state
-- ------------------------------------------------------------
CREATE TABLE t_order (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Order primary key',
    order_no        VARCHAR(32)     NOT NULL                COMMENT 'Unique order number',
    user_id         BIGINT          NOT NULL                COMMENT 'User ID who placed the order',
    product_id      BIGINT          NOT NULL                COMMENT 'Product ID',
    product_name    VARCHAR(200)    NOT NULL                COMMENT 'Product name snapshot',
    quantity        INT             NOT NULL DEFAULT 1      COMMENT 'Ordered quantity',
    unit_price      DECIMAL(12, 2)  NOT NULL                COMMENT 'Unit price at time of order',
    total_amount    DECIMAL(12, 2)  NOT NULL                COMMENT 'Total order amount',
    state           VARCHAR(50)     NOT NULL DEFAULT 'CREATED'
                                                            COMMENT 'Current state machine state',
    remark          VARCHAR(500)    NULL                    COMMENT 'Order remark / notes',
    payment_time    DATETIME        NULL                    COMMENT 'Timestamp when payment was confirmed',
    ship_time       DATETIME        NULL                    COMMENT 'Timestamp when order was shipped',
    receive_time    DATETIME        NULL                    COMMENT 'Timestamp when buyer confirmed receipt',
    cancel_time     DATETIME        NULL                    COMMENT 'Timestamp when order was cancelled',
    refund_time     DATETIME        NULL                    COMMENT 'Timestamp when refund was completed',
    timeout_at      DATETIME        NULL                    COMMENT 'Payment deadline (null = no deadline)',
    deleted         TINYINT(1)      NOT NULL DEFAULT 0      COMMENT 'Logical delete flag: 0=normal, 1=deleted',
    version         INT             NOT NULL DEFAULT 0      COMMENT 'Optimistic lock version',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                            COMMENT 'Record creation time',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                            COMMENT 'Record last update time',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id   (user_id),
    KEY idx_state     (state),
    KEY idx_timeout_at(timeout_at),
    KEY idx_create_time(create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order table';

-- ------------------------------------------------------------
--  Table: t_order_state_history
--  Immutable audit trail of every state transition
-- ------------------------------------------------------------
CREATE TABLE t_order_state_history (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    order_id        BIGINT          NOT NULL                COMMENT 'FK -> t_order.id',
    order_no        VARCHAR(32)     NOT NULL                COMMENT 'Denormalised order number for quick lookup',
    from_state      VARCHAR(50)     NOT NULL                COMMENT 'State before transition',
    to_state        VARCHAR(50)     NOT NULL                COMMENT 'State after transition',
    event           VARCHAR(50)     NOT NULL                COMMENT 'Event that triggered the transition',
    operator_id     BIGINT          NULL                    COMMENT 'User/system that triggered the event',
    operator_type   VARCHAR(20)     NULL DEFAULT 'USER'     COMMENT 'Operator type: USER | SYSTEM',
    remark          VARCHAR(500)    NULL                    COMMENT 'Optional transition remark',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                            COMMENT 'Transition timestamp',
    PRIMARY KEY (id),
    KEY idx_order_id   (order_id),
    KEY idx_order_no   (order_no),
    KEY idx_create_time(create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order state transition history';

-- ------------------------------------------------------------
--  Table: t_order_event_log
--  Fine-grained event log for debugging & replay
-- ------------------------------------------------------------
CREATE TABLE t_order_event_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    order_id        BIGINT          NOT NULL                COMMENT 'FK -> t_order.id',
    order_no        VARCHAR(32)     NOT NULL                COMMENT 'Denormalised order number',
    event           VARCHAR(50)     NOT NULL                COMMENT 'Event name',
    state_before    VARCHAR(50)     NOT NULL                COMMENT 'State before the event was sent',
    state_after     VARCHAR(50)     NULL                    COMMENT 'State after processing (null if rejected)',
    success         TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '1=accepted by state machine, 0=rejected',
    error_msg       VARCHAR(1000)   NULL                    COMMENT 'Error message when success=0',
    context_data    JSON            NULL                    COMMENT 'Serialised event context / payload',
    operator_id     BIGINT          NULL                    COMMENT 'Operator user ID',
    operator_type   VARCHAR(20)     NULL DEFAULT 'USER'     COMMENT 'Operator type: USER | SYSTEM',
    duration_ms     BIGINT          NULL                    COMMENT 'Processing duration in milliseconds',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                            COMMENT 'Event log creation time',
    PRIMARY KEY (id),
    KEY idx_order_id   (order_id),
    KEY idx_order_no   (order_no),
    KEY idx_event      (event),
    KEY idx_create_time(create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Order event log';
