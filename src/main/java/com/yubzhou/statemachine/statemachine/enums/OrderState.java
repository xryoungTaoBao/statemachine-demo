package com.yubzhou.statemachine.statemachine.enums;

/**
 * Order state machine states.
 */
public enum OrderState {

    /** Order created, awaiting payment. */
    PENDING_PAYMENT,

    /** Payment received, awaiting shipment. */
    PENDING_SHIPMENT,

    /** Order shipped, awaiting buyer confirmation. */
    SHIPPED,

    /** Buyer confirmed receipt; order is complete. */
    COMPLETED,

    /** Order was cancelled (by user or system timeout). */
    CANCELLED,

    /** Refund requested and being processed. */
    REFUNDING,

    /** Refund completed. */
    REFUNDED
}
