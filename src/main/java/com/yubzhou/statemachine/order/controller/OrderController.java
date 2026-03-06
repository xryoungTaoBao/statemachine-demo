package com.yubzhou.statemachine.order.controller;

import com.yubzhou.statemachine.common.result.Result;
import com.yubzhou.statemachine.order.dto.CreateOrderRequest;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.dto.OrderVO;
import com.yubzhou.statemachine.order.service.OrderEventLogService;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.order.service.OrderStateHistoryService;
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
@RequestMapping("/api/v1/orders")
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
    @GetMapping
    public Result<List<OrderVO>> listOrders(@RequestParam(required = false) String state,
                                             @RequestParam(required = false) Long userId) {
        if (state != null && !state.isEmpty()) {
            return Result.success(orderService.listByState(state));
        }
        if (userId != null) {
            return Result.success(orderService.listByUserId(userId));
        }
        return Result.success(orderService.listByState(null));
    }

    @Operation(summary = "Get available events for current order state")
    @GetMapping("/{orderId}/available-events")
    public Result<List<String>> getAvailableEvents(@PathVariable Long orderId) {
        return Result.success(orderService.getAvailableEvents(orderId));
    }

    @Operation(summary = "Submit an order")
    @PostMapping("/{orderId}/events/submit")
    public Result<OrderVO> submit(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.SUBMIT, request));
    }

    @Operation(summary = "Pay for an order")
    @PostMapping("/{orderId}/events/pay")
    public Result<OrderVO> pay(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.PAY, request));
    }

    @Operation(summary = "Cancel an order")
    @PostMapping("/{orderId}/events/cancel")
    public Result<OrderVO> cancel(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.CANCEL, request));
    }

    @Operation(summary = "Ship an order")
    @PostMapping("/{orderId}/events/ship")
    public Result<OrderVO> ship(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.SHIP, request));
    }

    @Operation(summary = "Confirm receipt of an order")
    @PostMapping("/{orderId}/events/receive")
    public Result<OrderVO> receive(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.RECEIVE, request));
    }

    @Operation(summary = "Complete an order")
    @PostMapping("/{orderId}/events/complete")
    public Result<OrderVO> complete(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.COMPLETE, request));
    }

    @Operation(summary = "Request a refund")
    @PostMapping("/{orderId}/events/refund")
    public Result<OrderVO> refund(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.REFUND, request));
    }

    @Operation(summary = "Approve a refund request")
    @PostMapping("/{orderId}/events/refund-approve")
    public Result<OrderVO> refundApprove(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.REFUND_APPROVE, request));
    }

    @Operation(summary = "Reject a refund request")
    @PostMapping("/{orderId}/events/refund-reject")
    public Result<OrderVO> refundReject(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        return Result.success(orderService.sendEvent(orderId, OrderEvent.REFUND_REJECT, request));
    }

    @Operation(summary = "Generic event trigger")
    @PostMapping("/{orderId}/events")
    public Result<OrderVO> triggerEvent(@PathVariable Long orderId, @RequestBody OrderEventRequest request) {
        if (request.getEvent() == null || request.getEvent().isEmpty()) {
            throw new com.yubzhou.statemachine.common.exception.BusinessException("Event name is required");
        }
        OrderEvent event = OrderEvent.valueOf(request.getEvent().toUpperCase());
        return Result.success(orderService.sendEvent(orderId, event, request));
    }

    @Operation(summary = "Get state transition history for an order")
    @GetMapping("/{orderId}/state-history")
    public Result<?> getStateHistory(@PathVariable Long orderId) {
        return Result.success(historyService.listByOrderId(orderId));
    }

    @Operation(summary = "Get event log for an order")
    @GetMapping("/{orderId}/event-logs")
    public Result<?> getEventLogs(@PathVariable Long orderId) {
        return Result.success(eventLogService.listByOrderId(orderId));
    }

    @Operation(summary = "Rollback order state")
    @PostMapping("/{orderId}/rollback")
    public Result<OrderVO> rollback(@PathVariable Long orderId,
                                    @RequestBody(required = false) OrderEventRequest request) {
        return Result.success(orderService.rollbackState(orderId, request));
    }
}
