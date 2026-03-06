package com.yubzhou.statemachine.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yubzhou.statemachine.order.entity.OrderEventLog;
import com.yubzhou.statemachine.order.mapper.OrderEventLogMapper;
import com.yubzhou.statemachine.order.service.OrderEventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Default implementation of {@link OrderEventLogService}.
 */
@Service
@RequiredArgsConstructor
public class OrderEventLogServiceImpl
        extends ServiceImpl<OrderEventLogMapper, OrderEventLog>
        implements OrderEventLogService {

    @Override
    public List<OrderEventLog> listByOrderId(Long orderId) {
        return list(new LambdaQueryWrapper<OrderEventLog>()
                .eq(OrderEventLog::getOrderId, orderId)
                .orderByDesc(OrderEventLog::getCreateTime));
    }

    @Override
    public void saveLog(OrderEventLog log) {
        save(log);
    }

    @Override
    public void deleteOlderThan(int retentionDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        remove(new LambdaQueryWrapper<OrderEventLog>()
                .lt(OrderEventLog::getCreateTime, cutoff));
    }
}
