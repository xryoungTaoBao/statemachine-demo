package com.yubzhou.statemachine.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yubzhou.statemachine.order.entity.OrderStateHistory;
import com.yubzhou.statemachine.order.mapper.OrderStateHistoryMapper;
import com.yubzhou.statemachine.order.service.OrderStateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link OrderStateHistoryService}.
 */
@Service
@RequiredArgsConstructor
public class OrderStateHistoryServiceImpl
        extends ServiceImpl<OrderStateHistoryMapper, OrderStateHistory>
        implements OrderStateHistoryService {

    @Override
    public List<OrderStateHistory> listByOrderId(Long orderId) {
        return list(new LambdaQueryWrapper<OrderStateHistory>()
                .eq(OrderStateHistory::getOrderId, orderId)
                .orderByDesc(OrderStateHistory::getCreateTime));
    }

    @Override
    public void saveHistory(OrderStateHistory history) {
        save(history);
    }
}
