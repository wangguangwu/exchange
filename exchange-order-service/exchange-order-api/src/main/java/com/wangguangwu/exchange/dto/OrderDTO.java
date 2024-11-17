package com.wangguangwu.exchange.dto;

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
public class OrderDTO {

    private Long id;
    private Long userId;
    private Long sequenceId;
    private Integer direction; // 0: BUY, 1: SELL
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal unfilledQuantity;
    private String status; // WAITING, PARTIAL, COMPLETE, CANCELLED
    private Long createdAt;
    private Long updatedAt;

}
