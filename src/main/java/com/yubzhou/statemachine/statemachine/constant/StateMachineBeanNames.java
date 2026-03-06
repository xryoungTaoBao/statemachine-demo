package com.yubzhou.statemachine.statemachine.constant;

/**
 * Bean names for state machine actions and guards.
 */
public final class StateMachineBeanNames {

    private StateMachineBeanNames() {}

    // Actions
    public static final String ACTION_SUBMIT_ORDER     = "submitOrderAction";
    public static final String ACTION_PAY_ORDER        = "payOrderAction";
    public static final String ACTION_SHIP_ORDER       = "shipOrderAction";
    public static final String ACTION_RECEIVE_ORDER    = "receiveOrderAction";
    public static final String ACTION_COMPLETE_ORDER   = "completeOrderAction";
    public static final String ACTION_CANCEL_ORDER     = "cancelOrderAction";
    public static final String ACTION_REFUND           = "refundAction";
    public static final String ACTION_REFUND_APPROVE   = "refundApproveAction";
    public static final String ACTION_REFUND_REJECT    = "refundRejectAction";
    public static final String ACTION_TIMEOUT          = "timeoutAction";

    // Guards
    public static final String GUARD_CAN_CANCEL        = "canCancelGuard";
    public static final String GUARD_CAN_REFUND        = "canRefundGuard";
    public static final String GUARD_REFUND_DEADLINE   = "refundDeadlineGuard";
}
