package com.yubzhou.statemachine.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO / view object returned to callers for an order.
 */
@Data
@Schema(description = "Order detail response")
public class OrderVO {

    @Schema(description = "Order ID")
    private Long id;

    @Schema(description = "Order number")
    private String orderNo;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "Product name")
    private String productName;

    @Schema(description = "Quantity")
    private Integer quantity;

    @Schema(description = "Order amount")
    private BigDecimal amount;

    @Schema(description = "Current state")
    private String state;

    @Schema(description = "Payment time")
    private LocalDateTime paymentTime;

    @Schema(description = "Ship time")
    private LocalDateTime shipTime;

    @Schema(description = "Receipt confirmation time")
    private LocalDateTime receiveTime;

    @Schema(description = "Complete time")
    private LocalDateTime completeTime;

    @Schema(description = "Cancel time")
    private LocalDateTime cancelTime;

    @Schema(description = "Cancel reason")
    private String cancelReason;

    @Schema(description = "Tracking number")
    private String trackingNo;

    @Schema(description = "Logistics company")
    private String logisticsCompany;

    @Schema(description = "Refund reason")
    private String refundReason;

    @Schema(description = "Refund amount")
    private BigDecimal refundAmount;

    @Schema(description = "Remark")
    private String remark;

    @Schema(description = "Creation time")
    private LocalDateTime createTime;

    @Schema(description = "Last update time")
    private LocalDateTime updateTime;
}
