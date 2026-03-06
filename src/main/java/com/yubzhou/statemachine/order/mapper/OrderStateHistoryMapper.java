package com.yubzhou.statemachine.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yubzhou.statemachine.order.entity.OrderStateHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for {@link OrderStateHistory}.
 */
@Mapper
public interface OrderStateHistoryMapper extends BaseMapper<OrderStateHistory> {
}
