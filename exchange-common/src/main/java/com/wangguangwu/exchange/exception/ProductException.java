package com.wangguangwu.exchange.exception;

import com.wangguangwu.exchange.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品服务异常。
 *
 * @author wangguangwu
 */
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductException extends RuntimeException {

    protected final Integer data;
    protected final String message;

    public ProductException() {
        this.data = ResponseEnum.PRODUCT_SERVER_EXCEPTION.getCode();
        this.message = ResponseEnum.PRODUCT_SERVER_EXCEPTION.getMessage();
    }

    public ProductException(String message) {
        this.data = ResponseEnum.PRODUCT_SERVER_EXCEPTION.getCode();
        this.message = message;
    }
}
