package com.yubzhou.statemachine.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO for sending an event (state transition) to an order's state machine.
 */
@Data
@Schema(description = "Order event request")
public class OrderEventRequest {

    @Schema(description = "Operator user ID", example = "1001")
    private Long operatorId;

    @Schema(description = "Operator type: USER or SYSTEM", example = "USER")
    private String operatorType = "USER";

    @Schema(description = "Optional remark for the transition")
    private String remark;

    @Schema(description = "Tracking number (for SHIP_ORDER event)")
    private String trackingNo;

    @Schema(description = "Carrier name (for SHIP_ORDER event)")
    private String carrier;

    @Schema(description = "Payment method (for PAY_ORDER event)")
    private String paymentMethod;

    @Schema(description = "Refund reason (for REQUEST_REFUND event)")
    private String refundReason;
}
