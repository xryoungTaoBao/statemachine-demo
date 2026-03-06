package com.yubzhou.statemachine.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Immutable audit trail of every order state transition.
 * Mapped to {@code t_order_state_history}.
 */
@Data
@Accessors(chain = true)
@TableName("t_order_state_history")
public class OrderStateHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private String fromState;

    private String toState;

    private String event;

    private Long operatorId;

    /** USER or SYSTEM */
    private String operatorType;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
