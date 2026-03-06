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

    @Schema(description = "Unit price")
    private BigDecimal unitPrice;

    @Schema(description = "Total amount")
    private BigDecimal totalAmount;

    @Schema(description = "Current state")
    private String state;

    @Schema(description = "Remark")
    private String remark;

    @Schema(description = "Payment time")
    private LocalDateTime paymentTime;

    @Schema(description = "Ship time")
    private LocalDateTime shipTime;

    @Schema(description = "Receipt confirmation time")
    private LocalDateTime receiveTime;

    @Schema(description = "Cancel time")
    private LocalDateTime cancelTime;

    @Schema(description = "Refund completion time")
    private LocalDateTime refundTime;

    @Schema(description = "Payment deadline")
    private LocalDateTime timeoutAt;

    @Schema(description = "Creation time")
    private LocalDateTime createTime;

    @Schema(description = "Last update time")
    private LocalDateTime updateTime;
}
