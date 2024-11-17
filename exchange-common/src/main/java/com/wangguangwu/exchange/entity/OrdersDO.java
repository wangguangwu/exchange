package com.wangguangwu.exchange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author wangguangwu
 * @since 2024-11-17
 */
@Getter
@Setter
@TableName("orders")
public class OrdersDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 定序ID
     */
    @TableField("sequence_id")
    private Long sequenceId;

    /**
     * 订单方向（0: 买, 1: 卖）
     */
    @TableField("direction")
    private Integer direction;

    /**
     * 订单价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 订单总数量
     */
    @TableField("quantity")
    private BigDecimal quantity;

    /**
     * 未成交数量
     */
    @TableField("unfilled_quantity")
    private BigDecimal unfilledQuantity;

    /**
     * 订单状态
     */
    @TableField("status")
    private String status;

    /**
     * 是否已删除（0: 未删除, 1: 已删除）
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;

    /**
     * 创建时间（时间戳）
     */
    @TableField("created_at")
    private Long createdAt;

    /**
     * 更新时间（时间戳）
     */
    @TableField("updated_at")
    private Long updatedAt;
}
