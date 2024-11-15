package com.wangguangwu.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangguangwu
 */
@Data
public class AssetDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资产类型ID
     */
    private Long assetTypeId;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;
}
