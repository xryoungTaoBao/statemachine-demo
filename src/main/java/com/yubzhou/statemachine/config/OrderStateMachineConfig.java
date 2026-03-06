package com.yubzhou.statemachine.config;

import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * Spring Statemachine 核心配置
 * <p>
 * 定义订单状态机的所有状态、子状态及状态转换关系。
 * 使用工厂模式支持多实例并发。
 * </p>
 */
@Slf4j
@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    /**
     * 状态机全局配置
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        config
            .withConfiguration()
                .autoStartup(false);
    }

    /**
     * 状态定义
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
            .withStates()
                // 初始状态：已创建
                .initial(OrderState.CREATED)
                // 终态
                .end(OrderState.COMPLETED)
                .end(OrderState.CANCELLED)
                .end(OrderState.CLOSED)
                // 普通状态
                .states(EnumSet.of(
                    OrderState.CREATED,
                    OrderState.PENDING,
                    OrderState.PAID,
                    OrderState.SHIPPED,
                    OrderState.RECEIVED,
                    OrderState.COMPLETED,
                    OrderState.CANCELLED,
                    OrderState.CLOSED,
                    OrderState.REFUNDING
                ))
            .and()
            // 退款子状态机
            .withStates()
                .parent(OrderState.REFUNDING)
                .initial(OrderState.REFUND_PENDING)
                .states(EnumSet.of(
                    OrderState.REFUND_PENDING,
                    OrderState.REFUND_APPROVED,
                    OrderState.REFUND_REJECTED
                ));
    }

    /**
     * 状态转换定义
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
            // 提交订单: CREATED -> PENDING
            .withExternal()
                .source(OrderState.CREATED).target(OrderState.PENDING)
                .event(OrderEvent.SUBMIT)
                .and()

            // 支付: PENDING -> PAID
            .withExternal()
                .source(OrderState.PENDING).target(OrderState.PAID)
                .event(OrderEvent.PAY)
                .and()

            // 取消订单: CREATED -> CANCELLED
            .withExternal()
                .source(OrderState.CREATED).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()

            // 取消订单: PENDING -> CANCELLED
            .withExternal()
                .source(OrderState.PENDING).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()

            // 支付超时: PENDING -> CLOSED
            .withExternal()
                .source(OrderState.PENDING).target(OrderState.CLOSED)
                .event(OrderEvent.TIMEOUT)
                .and()

            // 发货: PAID -> SHIPPED
            .withExternal()
                .source(OrderState.PAID).target(OrderState.SHIPPED)
                .event(OrderEvent.SHIP)
                .and()

            // 签收: SHIPPED -> RECEIVED
            .withExternal()
                .source(OrderState.SHIPPED).target(OrderState.RECEIVED)
                .event(OrderEvent.RECEIVE)
                .and()

            // 完成: RECEIVED -> COMPLETED
            .withExternal()
                .source(OrderState.RECEIVED).target(OrderState.COMPLETED)
                .event(OrderEvent.COMPLETE)
                .and()

            // 退货退款: RECEIVED -> CLOSED
            .withExternal()
                .source(OrderState.RECEIVED).target(OrderState.CLOSED)
                .event(OrderEvent.RETURN)
                .and()

            // 退款完成: COMPLETED -> CLOSED
            .withExternal()
                .source(OrderState.COMPLETED).target(OrderState.CLOSED)
                .event(OrderEvent.RETURN)
                .and()

            // 申请退款: PAID -> REFUNDING
            .withExternal()
                .source(OrderState.PAID).target(OrderState.REFUNDING)
                .event(OrderEvent.REFUND)
                .and()

            // 退款审批通过: REFUND_PENDING -> REFUND_APPROVED
            .withExternal()
                .source(OrderState.REFUND_PENDING).target(OrderState.REFUND_APPROVED)
                .event(OrderEvent.REFUND_APPROVE)
                .and()

            // 退款审批拒绝: REFUND_PENDING -> REFUND_REJECTED
            .withExternal()
                .source(OrderState.REFUND_PENDING).target(OrderState.REFUND_REJECTED)
                .event(OrderEvent.REFUND_REJECT)
                .and()

            // 退款完成（退款已批准 -> 已关闭）
            .withExternal()
                .source(OrderState.REFUND_APPROVED).target(OrderState.CLOSED)
                .event(OrderEvent.COMPLETE)
                .and()

            // 退款拒绝后回到已支付状态
            .withExternal()
                .source(OrderState.REFUND_REJECTED).target(OrderState.PAID)
                .event(OrderEvent.CANCEL);
    }
}
