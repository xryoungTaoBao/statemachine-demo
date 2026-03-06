package com.yubzhou.statemachine.order.converter;

import com.yubzhou.statemachine.order.dto.OrderVO;
import com.yubzhou.statemachine.order.entity.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Converts {@link Order} entities to {@link OrderVO} view objects.
 */
@Component
public class OrderConverter {

    public OrderVO toVO(Order order) {
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        return vo;
    }
}
