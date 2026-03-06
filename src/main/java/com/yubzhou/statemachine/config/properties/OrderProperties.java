package com.yubzhou.statemachine.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 订单配置属性
 * <p>
 * Note: This class binds to the same prefix ({@code statemachine.order}) as
 * {@link StateMachineProperties} and provides the same set of properties.
 * It exists as a dedicated bean for components that logically belong to the
 * order domain rather than the state-machine infrastructure layer.
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "statemachine.order")
public class OrderProperties {

    /** 状态机 ID 前缀 */
    private String machineIdPrefix = "order-sm-";

    /** 支付超时时间（分钟） */
    private int paymentTimeoutMinutes = 30;

    /** 退款截止天数 */
    private int refundDeadlineDays = 7;

    /** 自动确认收货天数 */
    private int autoReceiveDays = 15;

    /** 是否启用持久化 */
    private boolean persistEnabled = true;

    /** 上下文过期时间（小时） */
    private int contextExpireHours = 72;
}
