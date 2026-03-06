package com.yubzhou.statemachine.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yubzhou.statemachine.common.exception.BusinessException;
import com.yubzhou.statemachine.config.properties.StateMachineProperties;
import com.yubzhou.statemachine.order.converter.OrderConverter;
import com.yubzhou.statemachine.order.dto.CreateOrderRequest;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.dto.OrderVO;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.mapper.OrderMapper;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import com.yubzhou.statemachine.statemachine.service.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
    private final StateMachineProperties smProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderRequest request) {
        Order order = new Order()
                .setOrderNo(generateOrderNo())
                .setUserId(request.getUserId())
                .setProductId(request.getProductId())
                .setProductName(request.getProductName())
                .setQuantity(request.getQuantity())
                .setUnitPrice(request.getUnitPrice())
                .setTotalAmount(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .setState(OrderState.CREATED.name())
                .setRemark(request.getRemark())
                .setTimeoutAt(LocalDateTime.now().plusMinutes(smProperties.getPaymentTimeoutMinutes()));

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
        // Reload persisted order state
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
        return list(new LambdaQueryWrapper<Order>().eq(Order::getState, state))
                .stream().map(orderConverter::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime))
                .stream().map(orderConverter::toVO).collect(Collectors.toList());
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
