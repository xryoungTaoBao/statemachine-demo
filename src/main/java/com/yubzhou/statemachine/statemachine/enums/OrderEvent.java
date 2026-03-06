package com.yubzhou.statemachine.statemachine.enums;

/**
 * Order state machine events (triggers for state transitions).
 */
public enum OrderEvent {

    /** Customer places a new order. */
    CREATE_ORDER,

    /** Customer completes payment. */
    PAY_ORDER,

    /** Payment deadline exceeded – system auto-cancels. */
    PAYMENT_TIMEOUT,

    /** Warehouse ships the goods. */
    SHIP_ORDER,

    /** Buyer confirms receipt of goods. */
    CONFIRM_RECEIPT,

    /** System auto-confirms receipt after timeout. */
    AUTO_CONFIRM_RECEIPT,

    /** Buyer cancels the order. */
    CANCEL_ORDER,

    /** Buyer requests a refund. */
    REQUEST_REFUND,

    /** Staff / system approves the refund. */
    APPROVE_REFUND,

    /** Refund money is returned to buyer. */
    COMPLETE_REFUND,

    /** Staff / system rejects the refund request. */
    REJECT_REFUND
}
