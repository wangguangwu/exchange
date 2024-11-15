package com.wangguangwu.exchange.exception;

import com.wangguangwu.exchange.enums.AssetEnum;
import com.wangguangwu.exchange.enums.ResponseEnum;
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

    protected final Integer data;
    protected final String message;

    public AssetException() {
        this.data = ResponseEnum.ASSETS_SERVER_EXCEPTION.getCode();
        this.message = ResponseEnum.ASSETS_SERVER_EXCEPTION.getMessage();
    }

    public AssetException(String message) {
        this.data = ResponseEnum.ASSETS_SERVER_EXCEPTION.getCode();
        this.message = message;
    }
}
