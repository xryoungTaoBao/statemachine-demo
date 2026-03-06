package com.yubzhou.statemachine.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yubzhou.statemachine.order.entity.OrderEventLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for {@link OrderEventLog}.
 */
@Mapper
public interface OrderEventLogMapper extends BaseMapper<OrderEventLog> {
}
