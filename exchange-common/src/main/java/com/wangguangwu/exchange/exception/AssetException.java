package com.wangguangwu.exchange.exception;

import com.wangguangwu.exchange.enums.AssetEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 资产操作异常。
 * <p>
 * 用于描述资产操作失败的场景，包括用户 ID、资产类型和操作金额等详细信息。
 *
 * @author wangguangwu
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class AssetException extends RuntimeException {

    private final Long userId;
    private final AssetEnum assetId;
    private final BigDecimal amount;

    /**
     * 构造方法，提供详细上下文信息。
     *
     * @param message 错误描述
     * @param userId  用户 ID
     * @param assetId 资产类型
     * @param amount  操作金额
     */
    public AssetException(String message, Long userId, AssetEnum assetId, BigDecimal amount) {
        super(message);
        this.userId = userId;
        this.assetId = assetId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format(
                "AssetException{message='%s', userId=%d, assetId=%s, amount=%s}",
                getMessage(), userId, assetId, amount
        );
    }
}
