package com.yubzhou.statemachine.statemachine.constant;

/**
 * Keys used to store values in the Spring Statemachine extended state.
 */
public final class StateMachineContextKeys {

    private StateMachineContextKeys() {}

    public static final String ORDER_ID       = "ORDER_ID";
    public static final String ORDER_NO       = "ORDER_NO";
    public static final String USER_ID        = "USER_ID";
    public static final String OPERATOR_ID    = "OPERATOR_ID";
    public static final String OPERATOR_TYPE  = "OPERATOR_TYPE";
    public static final String REMARK         = "REMARK";
    public static final String CONTEXT_DATA   = "CONTEXT_DATA";
    public static final String TRACKING_NO    = "TRACKING_NO";
    public static final String CARRIER        = "CARRIER";
    public static final String PAYMENT_METHOD = "PAYMENT_METHOD";
    public static final String REFUND_REASON  = "REFUND_REASON";
}
