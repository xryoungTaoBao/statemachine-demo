package com.yubzhou.statemachine.management.controller;

import com.yubzhou.statemachine.common.result.Result;
import com.yubzhou.statemachine.management.service.OrderManagementService;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for back-office order management operations (ship, approve refund, etc.).
 */
@Tag(name = "Order Management API", description = "Back-office operations for order management")
@RestController
@RequestMapping("/api/v1/state-machine")
@RequiredArgsConstructor
public class OrderManagementController {

    private final OrderManagementService managementService;

    @Operation(summary = "Ship an order (warehouse staff)")
    @PostMapping("/{orderId}/ship")
    public Result<Void> shipOrder(@PathVariable Long orderId,
                                  @RequestBody OrderEventRequest request) {
        managementService.shipOrder(orderId, request);
        return Result.success();
    }

    @Operation(summary = "Approve a refund request")
    @PostMapping("/{orderId}/approve-refund")
    public Result<Void> approveRefund(@PathVariable Long orderId,
                                      @RequestBody OrderEventRequest request) {
        managementService.approveRefund(orderId, request);
        return Result.success();
    }

    @Operation(summary = "Complete / disburse a refund")
    @PostMapping("/{orderId}/complete-refund")
    public Result<Void> completeRefund(@PathVariable Long orderId,
                                       @RequestBody OrderEventRequest request) {
        managementService.completeRefund(orderId, request);
        return Result.success();
    }

    @Operation(summary = "Reject a refund request")
    @PostMapping("/{orderId}/reject-refund")
    public Result<Void> rejectRefund(@PathVariable Long orderId,
                                     @RequestBody OrderEventRequest request) {
        managementService.rejectRefund(orderId, request);
        return Result.success();
    }
}
