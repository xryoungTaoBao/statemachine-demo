package com.yubzhou.statemachine.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Fine-grained event log for debugging and replay.
 * Mapped to {@code t_order_event_log}.
 */
@Data
@Accessors(chain = true)
@TableName("t_order_event_log")
public class OrderEventLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private String event;

    private String stateBefore;

    private String stateAfter;

    /** 1 = accepted by state machine, 0 = rejected. */
    private Boolean success;

    private String errorMsg;

    /** JSON string with event context / payload. */
    private String contextData;

    private Long operatorId;

    /** USER or SYSTEM */
    private String operatorType;

    private Long durationMs;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
