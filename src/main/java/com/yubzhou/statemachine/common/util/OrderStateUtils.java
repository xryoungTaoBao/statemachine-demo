package com.yubzhou.statemachine.common.util;

import com.yubzhou.statemachine.statemachine.enums.OrderState;

import java.util.List;
import java.util.Map;

/**
 * Utilities related to order state transitions.
 */
public final class OrderStateUtils {

    private OrderStateUtils() {}

    /** States that are considered terminal (no further transitions possible). */
    private static final List<OrderState> TERMINAL_STATES =
            List.of(OrderState.COMPLETED, OrderState.CANCELLED, OrderState.CLOSED);

    /** Map of state -> human-readable Chinese label. */
    private static final Map<OrderState, String> LABELS = Map.ofEntries(
            Map.entry(OrderState.CREATED,          "已创建"),
            Map.entry(OrderState.PENDING,          "待支付"),
            Map.entry(OrderState.PAID,             "已支付"),
            Map.entry(OrderState.SHIPPED,          "已发货"),
            Map.entry(OrderState.RECEIVED,         "已签收"),
            Map.entry(OrderState.COMPLETED,        "已完成"),
            Map.entry(OrderState.CANCELLED,        "已取消"),
            Map.entry(OrderState.CLOSED,           "已关闭"),
            Map.entry(OrderState.REFUNDING,        "退款中"),
            Map.entry(OrderState.REFUND_PENDING,   "退款待审核"),
            Map.entry(OrderState.REFUND_APPROVED,  "退款已批准"),
            Map.entry(OrderState.REFUND_REJECTED,  "退款已拒绝")
    );

    public static boolean isTerminal(OrderState state) {
        return TERMINAL_STATES.contains(state);
    }

    public static String getLabel(OrderState state) {
        return LABELS.getOrDefault(state, state.name());
    }
}
