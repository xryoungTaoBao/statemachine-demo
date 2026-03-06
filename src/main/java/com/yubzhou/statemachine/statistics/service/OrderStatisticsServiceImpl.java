package com.yubzhou.statemachine.statistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of {@link OrderStatisticsService}.
 */
@Service
@RequiredArgsConstructor
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    private final OrderService orderService;

    @Override
    public Map<String, Long> countByState() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (OrderState state : OrderState.values()) {
            long count = orderService.count(new LambdaQueryWrapper<Order>()
                    .eq(Order::getState, state.name()));
            result.put(state.name(), count);
        }
        return result;
    }

    @Override
    public long totalCount() {
        return orderService.count();
    }
}
