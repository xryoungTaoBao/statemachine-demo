package com.yubzhou.statemachine.statemachine.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderState {

    /** 订单已创建 */
    CREATED(10, "已创建"),

    /** 待支付 */
    PENDING(20, "待支付"),

    /** 已支付 */
    PAID(30, "已支付"),

    /** 已发货 */
    SHIPPED(40, "已发货"),

    /** 已签收 */
    RECEIVED(50, "已签收"),

    /** 已完成 */
    COMPLETED(60, "已完成"),

    /** 已取消 */
    CANCELLED(70, "已取消"),

    /** 已关闭 */
    CLOSED(80, "已关闭"),

    /** 退款中 */
    REFUNDING(90, "退款中"),

    /** 退款待审核（子状态） */
    REFUND_PENDING(91, "退款待审核"),

    /** 退款已批准（子状态） */
    REFUND_APPROVED(92, "退款已批准"),

    /** 退款已拒绝（子状态） */
    REFUND_REJECTED(93, "退款已拒绝");

    @EnumValue
    private final int code;
    private final String desc;

    OrderState(int code, String desc) {
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
    public static OrderState fromCode(int code) {
        for (OrderState state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        throw new IllegalArgumentException("未知的订单状态编码: " + code);
    }
}
