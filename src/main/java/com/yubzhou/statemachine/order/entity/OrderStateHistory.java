package com.yubzhou.statemachine.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 订单状态变更历史实体，对应 t_order_state_history 表
 */
@Data
@Accessors(chain = true)
@TableName("t_order_state_history")
public class OrderStateHistory {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 源状态 */
    private String fromState;

    /** 目标状态 */
    private String toState;

    /** 触发事件 */
    private String event;

    /** 事件类型: NORMAL 正常, ROLLBACK 回滚 */
    private String eventType;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人名称 */
    private String operatorName;

    /** 备注 */
    private String remark;

    /** 状态持续时长(毫秒) */
    private Long durationMs;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
