package com.wangguangwu.assetcore.assets;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资产信息
 *
 * @author wangguangwu
 */
@AllArgsConstructor
@Data
public class Asset {

    /**
     * 可用余额
     */
    BigDecimal available;

    /**
     * 冻结余额
     */
    BigDecimal frozen;

    public Asset() {
        this(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
