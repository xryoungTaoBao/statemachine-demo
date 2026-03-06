package com.yubzhou.statemachine.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yubzhou.statemachine.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据状态分页查询订单
     */
    List<Order> selectByStatePage(@Param("state") String state);

    /**
     * 查询支付超时订单
     */
    List<Order> selectTimeoutOrders(@Param("timeoutBefore") LocalDateTime timeoutBefore);

    /**
     * 查询需要自动确认收货的订单
     */
    List<Order> selectAutoReceiveOrders(@Param("shipBefore") LocalDateTime shipBefore);
}
