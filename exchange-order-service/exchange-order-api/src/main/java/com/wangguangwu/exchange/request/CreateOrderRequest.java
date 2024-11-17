package com.wangguangwu.exchange.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wangguangwu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private Long userId;
    private Long sequenceId;
    private Integer direction;
    private BigDecimal price;
    private BigDecimal quantity;

}
