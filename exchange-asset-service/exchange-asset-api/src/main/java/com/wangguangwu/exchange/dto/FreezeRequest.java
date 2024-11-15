package com.wangguangwu.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangguangwu
 */
@Data
public class FreezeRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 资产类型ID
     */
    private Long assetTypeId;

    /**
     * 冻结或解冻的金额
     */
    private BigDecimal amount;

}
