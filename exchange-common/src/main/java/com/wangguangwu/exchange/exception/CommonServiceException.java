package com.wangguangwu.exchange.exception;

import com.wangguangwu.exchange.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用业务异常。
 *
 * @author wangguangwu
 */
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonServiceException extends RuntimeException {

    protected final Integer data;
    protected final String message;

    public CommonServiceException() {
        this.data = ResponseEnum.COMMON_SERVICE_EXCEPTION.getCode();
        this.message = ResponseEnum.COMMON_SERVICE_EXCEPTION.getMessage();
    }

    public CommonServiceException(String message) {
        this.data = ResponseEnum.COMMON_SERVICE_EXCEPTION.getCode();
        this.message = message;
    }
}
