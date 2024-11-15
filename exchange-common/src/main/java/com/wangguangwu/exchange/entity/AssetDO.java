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
 * 资产表
 * </p>
 *
 * @author wangguangwu
 * @since 2024-11-15
 */
@Getter
@Setter
@TableName("asset")
public class AssetDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产记录唯一标识
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
     * 可用余额
     */
    @TableField("available_balance")
    private BigDecimal availableBalance;

    /**
     * 冻结余额
     */
    @TableField("frozen_balance")
    private BigDecimal frozenBalance;

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
     * 记录创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 记录更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
