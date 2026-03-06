package com.yubzhou.statemachine.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order entity mapped to {@code t_order}.
 */
@Data
@Accessors(chain = true)
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    /** Current state – kept in sync with the state machine. */
    private String state;

    private String remark;

    private LocalDateTime paymentTime;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime cancelTime;

    private LocalDateTime refundTime;

    /** Payment deadline; null means no deadline. */
    private LocalDateTime timeoutAt;

    @TableLogic
    private Integer deleted;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
