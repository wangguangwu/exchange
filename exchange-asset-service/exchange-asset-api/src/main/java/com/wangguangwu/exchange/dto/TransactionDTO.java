package com.wangguangwu.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author wangguangwu
 */
@Data
public class TransactionDTO {

    private Long userId;
    private Long assetTypeId;
    private int transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private BigDecimal frozenBalanceAfter;
    private LocalDateTime createdAt;

}
