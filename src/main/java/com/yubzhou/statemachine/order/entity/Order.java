package com.yubzhou.statemachine.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体，对应 t_order 表
 */
@Data
@Accessors(chain = true)
@TableName("t_order")
public class Order {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 商品名称 */
    private String productName;

    /** 数量 */
    private Integer quantity;

    /** 订单金额 */
    private BigDecimal amount;

    /** 当前状态 */
    private String state;

    /** 支付时间 */
    private LocalDateTime paymentTime;

    /** 发货时间 */
    private LocalDateTime shipTime;

    /** 签收时间 */
    private LocalDateTime receiveTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 取消原因 */
    private String cancelReason;

    /** 物流单号 */
    private String trackingNo;

    /** 物流公司 */
    private String logisticsCompany;

    /** 退款原因 */
    private String refundReason;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 备注 */
    private String remark;

    /** 乐观锁版本 */
    @Version
    private Integer version;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
