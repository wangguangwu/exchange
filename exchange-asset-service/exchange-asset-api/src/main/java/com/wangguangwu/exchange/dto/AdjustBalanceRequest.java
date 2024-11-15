package com.wangguangwu.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangguangwu
 */
@Data
public class AdjustBalanceRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资产类型ID
     */
    private Long assetTypeId;

    /**
     * 调整的金额（正为增加，负为减少）
     */
    private BigDecimal amount;

}