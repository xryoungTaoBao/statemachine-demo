package com.yubzhou.statemachine.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yubzhou.statemachine.common.exception.BusinessException;
import com.yubzhou.statemachine.config.properties.RollbackProperties;
import com.yubzhou.statemachine.config.properties.StateMachineProperties;
import com.yubzhou.statemachine.order.converter.OrderConverter;
import com.yubzhou.statemachine.order.dto.CreateOrderRequest;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.dto.OrderVO;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.entity.OrderStateHistory;
import com.yubzhou.statemachine.order.mapper.OrderMapper;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.order.service.OrderStateHistoryService;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import com.yubzhou.statemachine.statemachine.service.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link OrderService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderConverter orderConverter;
    private final OrderStateMachineService stateMachineService;
    private final OrderStateHistoryService historyService;
    private final StateMachineProperties smProperties;
    private final RollbackProperties rollbackProperties;

    /** 订单号序号计数器（同一秒内防重） */
    private static final AtomicInteger ORDER_SEQUENCE = new AtomicInteger(0);
    /** 订单号日期格式 */
    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 每个状态可触发的事件列表（根据状态机配置生成） */
    private static final Map<OrderState, List<OrderEvent>> STATE_EVENTS = Map.ofEntries(
            Map.entry(OrderState.CREATED,          List.of(OrderEvent.SUBMIT, OrderEvent.CANCEL)),
            Map.entry(OrderState.PENDING,          List.of(OrderEvent.PAY, OrderEvent.CANCEL, OrderEvent.TIMEOUT)),
            Map.entry(OrderState.PAID,             List.of(OrderEvent.SHIP, OrderEvent.REFUND)),
            Map.entry(OrderState.SHIPPED,          List.of(OrderEvent.RECEIVE)),
            Map.entry(OrderState.RECEIVED,         List.of(OrderEvent.COMPLETE, OrderEvent.RETURN, OrderEvent.REFUND)),
            Map.entry(OrderState.COMPLETED,        List.of(OrderEvent.RETURN)),
            Map.entry(OrderState.REFUND_PENDING,   List.of(OrderEvent.REFUND_APPROVE, OrderEvent.REFUND_REJECT)),
            Map.entry(OrderState.REFUND_APPROVED,  List.of(OrderEvent.COMPLETE)),
            Map.entry(OrderState.REFUND_REJECTED,  List.of(OrderEvent.CANCEL))
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderRequest request) {
        Order order = new Order()
                .setOrderNo(generateOrderNo())
                .setUserId(request.getUserId())
                .setProductName(request.getProductName())
                .setQuantity(request.getQuantity())
                .setAmount(request.getAmount())
                .setState(OrderState.CREATED.name())
                .setRemark(request.getRemark());

        save(order);
        log.info("Order created: orderId={}, orderNo={}", order.getId(), order.getOrderNo());
        return orderConverter.toVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO sendEvent(Long orderId, OrderEvent event, OrderEventRequest request) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found: " + orderId);
        }
        stateMachineService.sendEvent(order, event, request);
        return orderConverter.toVO(getById(orderId));
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found: " + orderId);
        }
        return orderConverter.toVO(order);
    }

    @Override
    public List<OrderVO> listByState(String state) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (state != null && !state.isEmpty()) {
            wrapper.eq(Order::getState, state);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return list(wrapper).stream().map(orderConverter::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime))
                .stream().map(orderConverter::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO rollbackState(Long orderId, OrderEventRequest request) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found: " + orderId);
        }

        List<OrderStateHistory> recent = historyService.listRecentByOrderId(orderId, 1);
        if (recent.isEmpty()) {
            throw new BusinessException("No history found to rollback for order: " + orderId);
        }

        OrderStateHistory lastHistory = recent.get(0);
        String previousState = lastHistory.getFromState();

        // Update order state to previous state
        Order update = new Order().setId(orderId).setState(previousState);
        updateById(update);

        // Save rollback history record
        OrderStateHistory rollbackRecord = new OrderStateHistory()
                .setOrderId(orderId)
                .setOrderNo(order.getOrderNo())
                .setFromState(order.getState())
                .setToState(previousState)
                .setEvent("ROLLBACK")
                .setEventType("ROLLBACK")
                .setOperatorId(request != null ? request.getOperatorId() : null)
                .setOperatorName(request != null ? request.getOperatorName() : null)
                .setRemark(request != null ? request.getRemark() : "Manual rollback");
        historyService.saveHistory(rollbackRecord);

        log.info("Order {} rolled back from {} to {}", order.getOrderNo(), order.getState(), previousState);
        return orderConverter.toVO(getById(orderId));
    }

    @Override
    public List<String> getAvailableEvents(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found: " + orderId);
        }
        OrderState currentState = OrderState.valueOf(order.getState());
        List<OrderEvent> events = STATE_EVENTS.getOrDefault(currentState, List.of());
        return events.stream().map(OrderEvent::name).collect(Collectors.toList());
    }

    @Override
    public List<Order> listPendingTimeoutOrders(LocalDateTime createdBefore) {
        return baseMapper.selectTimeoutOrders(createdBefore);
    }

    @Override
    public List<Order> listShippedAutoReceiveOrders(LocalDateTime shippedBefore) {
        return baseMapper.selectAutoReceiveOrders(shippedBefore);
    }

    /**
     * 生成订单号：格式为 ORD + yyyyMMddHHmmss + 3位序号
     * <p>示例：ORD20260306143052001</p>
     * <p>使用 AtomicInteger 保证同一秒内序号不重复（超过999自动重置）</p>
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(ORDER_NO_FORMATTER);
        int seq = ORDER_SEQUENCE.incrementAndGet() % 1000;
        return String.format("ORD%s%03d", timestamp, seq);
    }
}
