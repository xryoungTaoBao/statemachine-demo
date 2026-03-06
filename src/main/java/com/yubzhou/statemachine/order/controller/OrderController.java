package com.yubzhou.statemachine.order.controller;

import com.yubzhou.statemachine.common.result.Result;
import com.yubzhou.statemachine.order.dto.CreateOrderRequest;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.dto.OrderVO;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.order.service.OrderStateHistoryService;
import com.yubzhou.statemachine.order.service.OrderEventLogService;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order lifecycle operations.
 */
@Tag(name = "Order API", description = "Order lifecycle management")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderStateHistoryService historyService;
    private final OrderEventLogService eventLogService;

    @Operation(summary = "Create a new order")
    @PostMapping
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrder(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderById(orderId));
    }

    @Operation(summary = "List orders by state")
    @GetMapping("/state/{state}")
    public Result<List<OrderVO>> listByState(@PathVariable String state) {
        return Result.success(orderService.listByState(state));
    }

    @Operation(summary = "List orders by user")
    @GetMapping("/user/{userId}")
    public Result<List<OrderVO>> listByUser(@PathVariable Long userId) {
        return Result.success(orderService.listByUserId(userId));
    }

    @Operation(summary = "Pay for an order")
    @PostMapping("/{orderId}/pay")
    public Result<OrderVO> pay(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.PAY, request));
    }

    @Operation(summary = "Ship an order")
    @PostMapping("/{orderId}/ship")
    public Result<OrderVO> ship(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.SHIP, request));
    }

    @Operation(summary = "Confirm receipt of an order")
    @PostMapping("/{orderId}/confirm-receipt")
    public Result<OrderVO> confirmReceipt(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.RECEIVE, request));
    }

    @Operation(summary = "Cancel an order")
    @PostMapping("/{orderId}/cancel")
    public Result<OrderVO> cancel(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.CANCEL, request));
    }

    @Operation(summary = "Request a refund")
    @PostMapping("/{orderId}/request-refund")
    public Result<OrderVO> requestRefund(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.REFUND, request));
    }

    @Operation(summary = "Complete a refund")
    @PostMapping("/{orderId}/complete-refund")
    public Result<OrderVO> completeRefund(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.COMPLETE, request));
    }

    @Operation(summary = "Reject a refund request")
    @PostMapping("/{orderId}/reject-refund")
    public Result<OrderVO> rejectRefund(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.REFUND_REJECT, request));
    }

    @Operation(summary = "Get state transition history for an order")
    @GetMapping("/{orderId}/history")
    public Result<?> getHistory(@PathVariable Long orderId) {
        return Result.success(historyService.listByOrderId(orderId));
    }

    @Operation(summary = "Get event log for an order")
    @GetMapping("/{orderId}/events")
    public Result<?> getEventLog(@PathVariable Long orderId) {
        return Result.success(eventLogService.listByOrderId(orderId));
    }
}
