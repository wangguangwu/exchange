package com.wangguangwu.exchange.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 资产交易记录表
 * </p>
 *
 * @author wangguangwu
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("asset_transaction")
public class AssetTransactionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户唯一标识
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 资产类型标识
     */
    @TableField("asset_type_id")
    private Long assetTypeId;

    /**
     * 交易类型：1=充值，2=提现，3=冻结，4=解冻
     */
    @TableField("transaction_type")
    private Integer transactionType;

    /**
     * 交易金额（正为增加，负为减少）
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 交易后的可用余额
     */
    @TableField("balance_after")
    private BigDecimal balanceAfter;

    /**
     * 交易后的冻结余额
     */
    @TableField("frozen_balance_after")
    private BigDecimal frozenBalanceAfter;

    /**
     * 软删除标识：0=未删除，1=已删除
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;

    /**
     * 乐观锁版本号
     */
    @TableField("version")
    @Version
    private Long version;

    /**
     * 交易时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
