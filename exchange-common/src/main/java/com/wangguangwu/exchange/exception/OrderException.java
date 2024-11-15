package com.wangguangwu.exchange.exception;

import com.wangguangwu.exchange.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单服务异常。
 *
 * @author wangguangwu
 */
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderException extends RuntimeException {

    protected final Integer data;
    protected final String message;

    public OrderException() {
        this.data = ResponseEnum.ORDER_SERVER_EXCEPTION.getCode();
        this.message = ResponseEnum.ORDER_SERVER_EXCEPTION.getMessage();
    }

    public OrderException(String message) {
        this.data = ResponseEnum.ORDER_SERVER_EXCEPTION.getCode();
        this.message = message;
    }
}
