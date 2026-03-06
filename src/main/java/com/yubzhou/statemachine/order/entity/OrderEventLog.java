package com.yubzhou.statemachine.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 订单事件处理日志实体，对应 t_order_event_log 表
 */
@Data
@Accessors(chain = true)
@TableName("t_order_event_log")
public class OrderEventLog {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 事件类型 */
    private String event;

    /** 事件携带数据（JSON） */
    private String eventData;

    /** 当前状态 */
    private String currentState;

    /** 目标状态 */
    private String targetState;

    /** 处理结果: ACCEPTED / DENIED / DEFERRED */
    private String result;

    /** 错误码 */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    /** 执行耗时(毫秒) */
    private Long executionTimeMs;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
