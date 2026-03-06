package com.yubzhou.statemachine.statemachine.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 订单事件枚举
 */
@Getter
public enum OrderEvent {

    /** 提交订单 */
    SUBMIT(10, "提交订单"),

    /** 支付 */
    PAY(20, "支付"),

    /** 取消订单 */
    CANCEL(30, "取消订单"),

    /** 支付超时 */
    TIMEOUT(40, "支付超时"),

    /** 发货 */
    SHIP(50, "发货"),

    /** 签收 */
    RECEIVE(60, "签收"),

    /** 完成 */
    COMPLETE(70, "完成"),

    /** 申请退款 */
    REFUND(80, "申请退款"),

    /** 退款审批通过 */
    REFUND_APPROVE(81, "退款审批通过"),

    /** 退款审批拒绝 */
    REFUND_REJECT(82, "退款审批拒绝"),

    /** 退货 */
    RETURN(90, "退货");

    @EnumValue
    private final int code;
    private final String desc;

    OrderEvent(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    /**
     * 根据编码获取枚举
     */
    public static OrderEvent fromCode(int code) {
        for (OrderEvent event : values()) {
            if (event.code == code) {
                return event;
            }
        }
        throw new IllegalArgumentException("未知的订单事件编码: " + code);
    }
}
