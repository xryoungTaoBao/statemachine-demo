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
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no            VARCHAR(32)     NOT NULL                COMMENT '订单号',
    user_id             BIGINT          NOT NULL                COMMENT '用户ID',
    product_name        VARCHAR(200)    NOT NULL                COMMENT '商品名称',
    quantity            INT             NOT NULL DEFAULT 1      COMMENT '数量',
    amount              DECIMAL(12, 2)  NOT NULL                COMMENT '订单金额',
    state               VARCHAR(50)     NOT NULL DEFAULT 'CREATED'
                                                                COMMENT '当前状态',
    payment_time        DATETIME        NULL                    COMMENT '支付时间',
    ship_time           DATETIME        NULL                    COMMENT '发货时间',
    receive_time        DATETIME        NULL                    COMMENT '签收时间',
    complete_time       DATETIME        NULL                    COMMENT '完成时间',
    cancel_time         DATETIME        NULL                    COMMENT '取消时间',
    cancel_reason       VARCHAR(500)    NULL                    COMMENT '取消原因',
    tracking_no         VARCHAR(100)    NULL                    COMMENT '物流单号',
    logistics_company   VARCHAR(100)    NULL                    COMMENT '物流公司',
    refund_reason       VARCHAR(500)    NULL                    COMMENT '退款原因',
    refund_amount       DECIMAL(12, 2)  NULL                    COMMENT '退款金额',
    remark              VARCHAR(500)    NULL                    COMMENT '备注',
    version             INT             NOT NULL DEFAULT 0      COMMENT '乐观锁版本',
    deleted             TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '逻辑删除: 0=正常, 1=已删除',
    create_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                                COMMENT '创建时间',
    update_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                                                COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no   (order_no),
    KEY idx_user_id          (user_id),
    KEY idx_state            (state),
    KEY idx_create_time      (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- ------------------------------------------------------------
--  Table: t_order_state_history
--  Immutable audit trail of every state transition
-- ------------------------------------------------------------
CREATE TABLE t_order_state_history (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id        BIGINT          NOT NULL                COMMENT '订单ID',
    order_no        VARCHAR(32)     NOT NULL                COMMENT '订单号',
    from_state      VARCHAR(50)     NOT NULL                COMMENT '源状态',
    to_state        VARCHAR(50)     NOT NULL                COMMENT '目标状态',
    event           VARCHAR(50)     NOT NULL                COMMENT '触发事件',
    event_type      VARCHAR(20)     NOT NULL DEFAULT 'NORMAL'
                                                            COMMENT '事件类型: NORMAL 正常, ROLLBACK 回滚',
    operator_id     BIGINT          NULL                    COMMENT '操作人ID',
    operator_name   VARCHAR(100)    NULL                    COMMENT '操作人名称',
    remark          VARCHAR(500)    NULL                    COMMENT '备注',
    duration_ms     BIGINT          NULL                    COMMENT '状态持续时长(毫秒)',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                            COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_order_id    (order_id),
    KEY idx_order_no    (order_no),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态变更历史';

-- ------------------------------------------------------------
--  Table: t_order_event_log
--  Fine-grained event log for debugging & replay
-- ------------------------------------------------------------
CREATE TABLE t_order_event_log (
    id                  BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id            BIGINT          NOT NULL                COMMENT '订单ID',
    order_no            VARCHAR(32)     NOT NULL                COMMENT '订单号',
    event               VARCHAR(50)     NOT NULL                COMMENT '事件类型',
    event_data          JSON            NULL                    COMMENT '事件携带数据（JSON）',
    current_state       VARCHAR(50)     NOT NULL                COMMENT '当前状态',
    target_state        VARCHAR(50)     NULL                    COMMENT '目标状态',
    result              VARCHAR(20)     NOT NULL DEFAULT 'ACCEPTED'
                                                                COMMENT '处理结果: ACCEPTED / DENIED / DEFERRED',
    error_code          VARCHAR(100)    NULL                    COMMENT '错误码',
    error_message       VARCHAR(1000)   NULL                    COMMENT '错误信息',
    execution_time_ms   BIGINT          NULL                    COMMENT '执行耗时(毫秒)',
    create_time         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                                COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_order_id    (order_id),
    KEY idx_order_no    (order_no),
    KEY idx_event       (event),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单事件处理日志';
