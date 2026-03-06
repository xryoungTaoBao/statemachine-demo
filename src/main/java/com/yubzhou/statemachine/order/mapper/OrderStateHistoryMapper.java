package com.yubzhou.statemachine.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yubzhou.statemachine.order.entity.OrderStateHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单状态变更历史 Mapper 接口
 */
@Mapper
public interface OrderStateHistoryMapper extends BaseMapper<OrderStateHistory> {

    /**
     * 根据订单ID查询状态变更历史
     */
    List<OrderStateHistory> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询最近N条状态变更历史（用于回滚）
     */
    List<OrderStateHistory> selectRecentByOrderId(@Param("orderId") Long orderId, @Param("limit") int limit);
}
