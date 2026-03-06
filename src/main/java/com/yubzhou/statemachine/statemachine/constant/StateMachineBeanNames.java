package com.yubzhou.statemachine.statemachine.constant;

/**
 * Bean names for state machine actions and guards.
 */
public final class StateMachineBeanNames {

    private StateMachineBeanNames() {}

    // Actions
    public static final String ACTION_CREATE_ORDER     = "createOrderAction";
    public static final String ACTION_PAY_ORDER        = "payOrderAction";
    public static final String ACTION_SHIP_ORDER       = "shipOrderAction";
    public static final String ACTION_CONFIRM_RECEIPT  = "confirmReceiptAction";
    public static final String ACTION_CANCEL_ORDER     = "cancelOrderAction";
    public static final String ACTION_REQUEST_REFUND   = "requestRefundAction";
    public static final String ACTION_APPROVE_REFUND   = "approveRefundAction";
    public static final String ACTION_COMPLETE_REFUND  = "completeRefundAction";
    public static final String ACTION_REJECT_REFUND    = "rejectRefundAction";
    public static final String ACTION_PAYMENT_TIMEOUT  = "paymentTimeoutAction";

    // Guards
    public static final String GUARD_CAN_CANCEL        = "canCancelGuard";
    public static final String GUARD_CAN_REFUND        = "canRefundGuard";
    public static final String GUARD_REFUND_DEADLINE   = "refundDeadlineGuard";
}
