package com.wangguangwu.exchange.enums;

/**
 * 资产对应的操作只有一种类型 -> 转账
 * <p>
 * 转账类型
 *
 * @author wangguangwu
 */
public enum Transfer {

    /**
     * 可用转可用
     */
    AVAILABLE_TO_AVAILABLE,

    /**
     * 可用转冻结
     */
    AVAILABLE_TO_FROZEN,

    /**
     * 冻结转可用
     */
    FROZEN_TO_AVAILABLE;

}
