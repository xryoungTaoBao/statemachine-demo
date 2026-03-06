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

    @Schema(description = "Operator name", example = "张三")
    private String operatorName;

    @Schema(description = "Optional remark for the transition")
    private String remark;

    @Schema(description = "Tracking number (for SHIP event)")
    private String trackingNo;

    @Schema(description = "Carrier name (for SHIP event)")
    private String carrier;

    @Schema(description = "Payment method (for PAY event)")
    private String paymentMethod;

    @Schema(description = "Refund reason (for REFUND event)")
    private String refundReason;

    @Schema(description = "Cancel reason (for CANCEL event)")
    private String cancelReason;

    @Schema(description = "Event name (for generic event endpoint)", example = "PAY")
    private String event;
}
