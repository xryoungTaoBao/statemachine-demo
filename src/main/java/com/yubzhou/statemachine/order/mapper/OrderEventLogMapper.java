package com.yubzhou.statemachine.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yubzhou.statemachine.order.entity.OrderEventLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单事件处理日志 Mapper 接口
 */
@Mapper
public interface OrderEventLogMapper extends BaseMapper<OrderEventLog> {

    /**
     * 根据订单ID查询事件日志
     */
    List<OrderEventLog> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 清理过期日志
     */
    int deleteByCreateTimeBefore(@Param("before") LocalDateTime before);
}
