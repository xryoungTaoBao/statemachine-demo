package com.yubzhou.statemachine.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for creating a new order.
 */
@Data
@Schema(description = "Create order request")
public class CreateOrderRequest {

    @NotNull
    @Schema(description = "User ID", example = "1001")
    private Long userId;

    @NotBlank
    @Schema(description = "Product name", example = "Wireless Headphones")
    private String productName;

    @NotNull
    @Min(1)
    @Schema(description = "Quantity", example = "1")
    private Integer quantity;

    @NotNull
    @Positive
    @Schema(description = "Order amount", example = "299.00")
    private BigDecimal amount;

    @Schema(description = "Order remark")
    private String remark;
}
