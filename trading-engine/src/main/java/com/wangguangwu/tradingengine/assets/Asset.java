package com.wangguangwu.tradingengine.assets;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wangguangwu
 */
@AllArgsConstructor
public class Assets {

    /**
     * 可用余额
     */
    BigDecimal available;

    /**
     * 冻结余额
     */
    BigDecimal frozen;

    public Assets() {
        this(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
