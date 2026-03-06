package com.yubzhou.statemachine.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yubzhou.statemachine.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for {@link Order}.
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
