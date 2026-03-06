package com.yubzhou.statemachine.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the order state machine.
 */
@Data
@Component
@ConfigurationProperties(prefix = "statemachine.order")
public class StateMachineProperties {

    /** Prefix for state machine IDs in Redis. */
    private String machineIdPrefix = "order-sm-";

    /** Minutes before an unpaid order is auto-cancelled. */
    private int paymentTimeoutMinutes = 30;

    /** Days after receipt within which a refund can be requested. */
    private int refundDeadlineDays = 7;

    /** Days after shipment before auto-receipt confirmation. */
    private int autoReceiveDays = 15;

    /** Whether to persist state machine context to Redis. */
    private boolean persistEnabled = true;

    /** Hours to keep statemachine context in Redis. */
    private int contextExpireHours = 72;
}
